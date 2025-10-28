#include <fstream>
#include <iostream>
#include <algorithm>
#include <cstdlib>
#include <limits>
#include <iomanip>
#include <random>
#include <filesystem>

#include "main.h"

using namespace std;

vector<string> wrong_answers{}, wrong_translations{}, phrases_list{}, translation_list{};
long int randomIndex{};

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

// skickar tillbaka listorna ifall det behövs
void readFile(string const& fileName, string const& language_to_write_in)
{
    ifstream file{fileName + ".txt"};
    string line;

    if(!file.is_open())
    {
        throw invalid_argument("File doesn't exist!");
    }

    while(getline(file, line))
    {
        auto pos = line.find(':');
        if(pos != string::npos)
        {
            string phrase, translation;

            if(language_to_write_in == "translation")
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

            phrases_list.push_back(phrase);
            translation_list.push_back(translation);
        }
    }
    file.close();
}

// skickar true om svaret var rätt, false om det var fel
// tar även bort ordet om det var rätt svaret och lägger till i wrong containers om man
// svarade fel
bool compare(string userInput)
{    
    string correctAnswer = translation_list.at(randomIndex);
    string phrase = phrases_list.at(randomIndex);

    phrases_list.erase(phrases_list.begin() + randomIndex);
    translation_list.erase(translation_list.begin() + randomIndex);

    clean_string(userInput);

    to_lower(userInput);
    to_lower(correctAnswer);

    if(userInput != correctAnswer)
    {
        wrong_answers.push_back(phrase);
        wrong_translations.push_back(correctAnswer);
        return false;
    }

    return true;
}

// skickar true om man är klar, annars false
bool check_empty()
{
    if(phrases_list.empty())
    {
        phrases_list = std::move(wrong_answers);
        translation_list = std::move(wrong_translations);

        if(!phrases_list.empty())
        {
            return true;
        }
    }
    return false;
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
    while(!phrases_list.empty())
    {
        randomIndex = random(phrases_list.size() - 1);

        return phrases_list.at(randomIndex);
    }
    return string{};
}

bool writeToFile(string const& fileName, string const& contentToWrite)
{
    ofstream file{fileName + ".txt", ios::app};

    file << contentToWrite << endl;
    return true;
}