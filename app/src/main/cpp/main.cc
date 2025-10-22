#include <fstream>
#include <iostream>
#include <string>
#include <vector>
#include <algorithm>
#include <cstdlib>
#include <limits>
#include <iomanip>
#include <random>

using namespace std;

vector<string> wrong_answers{}, wrong_translations{};
int wrongCount{}, wordCount{};
bool firstRound{true}, cleared{false};

// skriver ut hexadecimala ASCII koden för varje tecken
void debug_string(string const& s) 
{
    cout << "[";
    for (unsigned char c : s) 
    {
        cout << hex << (int)c << " ";
    }
    cout << "]\n";
}

void clear_terminal(bool clear)
{
    if(clear)
    {
        system("clear");
    }
}

void clean_string(string & s)
{
    while (!s.empty() && (s.back() == L'\r' || s.back() == L'\n')) {
        s.pop_back();
    }

    std::replace_if(s.begin(), s.end(), [](wchar_t c) {
        return std::iswspace(c) && c != L' ';
    }, L' ');
}

string to_lower(string & word)
{
    transform(word.begin(), word.end(), word.begin(),
             [](unsigned char c)
             {
                return tolower(c);
             });
    return word;
}

void trim_white_space(string & phrase, string & translation)
{
    phrase.erase(0, phrase.find_first_not_of(" \t"));
    phrase.erase(phrase.find_last_not_of(" \t") + 1);
    translation.erase(0, translation.find_first_not_of(" \t"));
    translation.erase(translation.find_last_not_of(" \t") + 1);

    clean_string(phrase);
    clean_string(translation);
}

pair<string, string> readfile(string const& fileName, string const& language_to_write_in,
              vector<string> & phrases, vector<string> & translations)
{
    string filePath{fileName};

    if(fileName.size() <= 3)
    {
        filePath += ".txt";
    }
    else if(fileName.substr(fileName.size() - 4) != ".txt")
    {
        filePath += ".txt";
    }

    ifstream file{filePath};
    string line, message{"Skriv översättningen för ordet som skrivs ut"}, redo_message{"Träna på dom ord du hade fel på"};

    if(!file.is_open())
    {
        throw invalid_argument("Filen finns inte!");
    }

    while(getline(file, line))
    {
        auto pos = line.find(':');
        if(pos != string::npos)
        {
            string phrase, translation;

            if(language_to_write_in == "spanish" || language_to_write_in == "no_clear")
            {
                phrase = line.substr(0, pos);
                translation = line.substr(pos + 1);
            }
            else
            {
                translation = line.substr(0, pos);
                phrase = line.substr(pos + 1);
            }

            trim_white_space(phrase, translation);

            phrases.push_back(phrase);
            translations.push_back(translation);
        }
        else
        {
            message = line;
            if(getline(file, line))
            {
                redo_message = line;
            }
        }
    }
    file.close();

    return make_pair(message, redo_message);
}

void printfile(string const& fileName, vector<string> const& phrases, vector<string> const& translation)
{
    for(size_t i = 0; i < phrases.size(); i++)
    {
        cout << left << setw(20) << phrases.at(i)
             << right << translation.at(i) << "\n"
             << "￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣\n";
    }
}

void compare(string userInput, int randomIndex, 
             vector<string> & phrases, vector<string> & translations)
{    
    string correctAnswer = translations.at(randomIndex);

    clean_string(userInput);

    to_lower(userInput);
    to_lower(correctAnswer);

    if(userInput != correctAnswer)
    {
        cout << "Fel svar!\n" 
             << phrases.at(randomIndex) << " = " 
             << translations.at(randomIndex) << "\n\n"; 

        wrong_answers.push_back(phrases.at(randomIndex));
        wrong_translations.push_back(translations.at(randomIndex));
    }
    else
    {
        cout << "Rätt!\n\n";
    }
}


void check_empty(vector<string> & phrases, vector<string> & translation, string redo_message)
{
    if(phrases.empty())
    {
        if(firstRound)
        {
            wrongCount += wrong_answers.size();
            firstRound = false;
        }

        phrases = std::move(wrong_answers);
        translation = std::move(wrong_translations);

        if(!phrases.empty())
        {            
            if(!cleared)
            {
                cout << "\033[2J\033[H";
                cleared = true;
            }

            cout << "_______________________________\n" << redo_message << "\n\n";
        }
    }
}

long int random(int max)
{
    random_device r;
    default_random_engine e1(r());
    uniform_int_distribution<int> uniform_dist(0, max);
    
    return uniform_dist(e1);
}

void run(vector<string> & phrases, vector<string> & translation, bool clear, string const& redo_message)
{
    string userInput;

    while(!phrases.empty())
    {
        long int randomIndex{random(phrases.size() - 1)};
        cout << phrases.at(randomIndex) << ": ";

        getline(cin, userInput);

        compare(userInput, randomIndex, phrases, translation);

        phrases.erase(phrases.begin() + randomIndex);
        translation.erase(translation.begin() + randomIndex);        

        check_empty(phrases, translation, redo_message);
    }
}

pair<string, string> initialize(int argc, char* argv[], vector<string> & phrases, vector<string> & translation, bool clear)
{
    pair<string, string> messages{};

    if(argc >= 3 && (string(argv[argc - 2]) == "swedish" || string(argv[argc - 1]) == "swedish"))
    {
        messages = readfile(argv[1], string(argv[argc - 2]), phrases, translation);  
    }
    else 
    {
        messages = readfile(argv[1], "spanish", phrases, translation);
    }

    clear_terminal(clear);

    if(argc >= 3 && string(argv[2]) == "list")
    {
        printfile(argv[2], phrases, translation);
    }

    wordCount = phrases.size();

    return messages;
}

int main(int argc, char* argv[])
{
    vector<string> phrases, translation;
    string instruction{}, redo_message{};
    pair<string, string> messages{};
    
    bool clear{string(argv[argc - 1]) != "no_clear"};   

    try
    {
        messages = initialize(argc, argv, phrases, translation, clear);
    }
    catch(const std::exception& e)
    {
        std::cerr << e.what() << '\n';
        return EXIT_FAILURE;
    }

    instruction = messages.first;
    redo_message = messages.second;
    
    cout << instruction << "\n\n";

    run(phrases, translation, clear, redo_message);

    clear_terminal(clear);

    cout << "Klar!\nTotalt hade du " << wrongCount << " fel av " << wordCount << " ord\n";
    
    return 0;
}