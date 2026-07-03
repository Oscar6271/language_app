#include <fstream>
#include <iostream>
#include <algorithm>
#include <cstdlib>
#include <limits>
#include <iomanip>
#include <random>
#include <cstdio>

#include "../headers/setup.h"
#include "../headers/library.h"

using namespace std;

vector<string> wrong_answers, wrong_translations, phrases_list, translation_list, phrases_list_copy, translation_list_copy;
vector<char> seperators{':', ','};
vector<int> indexes{};

long int randomIndex;
bool rewriteFile_var = false;

enum codes
{
    EMPTY,
    NOT_EMPTY,
    FIRST_TIME_DONE
};

void clear_lists()
{
    wrong_translations.clear();
    wrong_answers.clear();
    phrases_list.clear();
    translation_list.clear();
}

void split_string(vector<string> & phrases, vector<string> & translations,
                  size_t pos, bool const write_in_swedish, string const& line)
{
    string phrase, translation;

    if(!write_in_swedish)
    {
        phrase = line.substr(0, pos);
        translation = line.substr(pos + 1);
    }
    else
    {
        translation = line.substr(0, pos);
        phrase = line.substr(pos + 1);
    }

    trim_white_space(phrase);
    trim_white_space(translation);

    phrases.push_back(phrase);
    translations.push_back(translation);

    phrases_list_copy.push_back(phrase);
    translation_list_copy.push_back(translation);
}

string make_filePath(string const& fileName)
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

    return filePath;
}

int readFile(string const& fileName, string const& write_in_swedish)
{
    ifstream file{make_filePath(fileName)};
    bool write_in_original {write_in_swedish == "original"};

    if(!file.is_open())
    {
        throw invalid_argument("Filen finns inte!");
    }

    clear_lists();
    string line;

    while(getline(file, line))
    {
        if(line == "")
        {
            continue;
        }
        int current_seperator = 0;

        split_string:
        auto pos = line.find(seperators.at(current_seperator));

        if(pos != string::npos)
        {
            split_string(phrases_list, translation_list, pos, write_in_original, line);
        }
        else if(end_of_file(pos, current_seperator, seperators.size()))
        {
            break;
        }
        else
        {
            current_seperator++;
            goto split_string;
        }
    }

    file.close();

    return phrases_list.size();
}

string printAlternatives(vector<string> answers)
{
    string alternatives{};
    for(string answer : answers)
    {
        alternatives += answer + " and ";
    }

    // tar bort det sista " and "
    return alternatives.substr(0, alternatives.size() - 5);
}

// skickar true om svaret var rätt, false om det var fel
// tar även bort ordet om det var rätt svaret och lägger till i wrong containers om man
// svarade fel
string compare(string userInput)
{
    string fullAnswer = translation_list.at(randomIndex);
    string phrase = phrases_list.at(randomIndex);

    phrases_list.erase(phrases_list.begin() + randomIndex);
    translation_list.erase(translation_list.begin() + randomIndex);

    trim_white_space(userInput);
    to_lower(userInput);
    to_lower(fullAnswer);

    string alternative = ignore_explanation(fullAnswer);

    if (userInput == fullAnswer || userInput == alternative)
    {
        return "Correct!";
    }
    vector<string> answers = find_alternatives(fullAnswer);

    for(string answer : answers)
    {
        string explanation = ignore_explanation(answer);
        if(userInput == answer || userInput == explanation)
        {
            return "Correct! " + phrase + " means " + printAlternatives(answers);
        }
    }

    wrong_answers.push_back(phrase);
    wrong_translations.push_back(fullAnswer);
    return "Wrong, " + phrase + " means " + fullAnswer;
}

int check_empty()
{
    if(phrases_list.empty())
    {
        phrases_list = std::move(wrong_answers);
        translation_list = std::move(wrong_translations);

        // om listan fortfarande är tom
        if(phrases_list.empty())
        {
            return EMPTY;
        }

        // om listan inte är tom längre, första omgången är ändå klar
        return FIRST_TIME_DONE;
    }

    return NOT_EMPTY;
}

long int random(int max)
{
    random_device r;
    default_random_engine e1(r());
    uniform_int_distribution<int> uniform_dist(0, max);
    
    return uniform_dist(e1);
}

// skickar tillbaka ordet
string pickWord()
{
    if(!phrases_list.empty())
    {
        randomIndex = random(phrases_list.size() - 1);
        indexes.push_back(randomIndex);

        return phrases_list.at(randomIndex);
    }
    return string{};
}

void writeToFile(string const& fileName,
                 string contentToWrite, bool append)
{
    ofstream file;

    if(append)
    {
        file.open(make_filePath(fileName), ios::app);
    }
    else
    {
        file.open(make_filePath(fileName));
    }
    file << contentToWrite << endl;
}

string printFile(string const& fileName)
{
    string result{}, line{};
    ifstream file{fileName + ".txt"};

    if(!file.is_open())
    {
        return "Couldn't open file " + fileName;
    }

    while(getline(file, line))
    {
        result += line + "\n";
    }
    result = result.substr(0, result.size() - 1);

    file.close();

    return result;
}

int check_size()
{
    return phrases_list.size();
}

void addAlternative(string newAlternative, string correctWord)
{
    rewriteFile_var = true;
    clean_wrong_lists();

    auto it = find(phrases_list_copy.begin(), phrases_list_copy.end(), correctWord);
    int previousIndex = distance(phrases_list_copy.begin(), it);

    if(previousIndex < translation_list_copy.size())
    {
        translation_list_copy[previousIndex] += " / " + newAlternative;
    }
}

bool rewriteFile(string const& fileName)
{
    if(!rewriteFile_var)
    {
        return false;
    }

    string content{};

    for(int i = 0; i < phrases_list_copy.size(); i++)
    {
        content += trim_white_space(phrases_list_copy.at(i)) + " : " + trim_white_space(translation_list_copy.at(i)) + '\n';
    }

    writeToFile(fileName, content, false);

    return true;
}

void clean_wrong_lists()
{
    if(!wrong_answers.empty() && !wrong_translations.empty())
    {
        wrong_translations.pop_back();
        wrong_answers.pop_back();
    }
}

int wordsLeft()
{
    return phrases_list.size();
}

int mistakes()
{
    return wrong_answers.size();
}