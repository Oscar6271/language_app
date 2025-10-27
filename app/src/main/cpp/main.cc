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
pair<vector<string>, vector<string>> readFile(string const& fileName, string const& language_to_write_in)
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
    return make_pair(phrases_list, translation_list);
}

// skickar true om svaret var rätt, false om det var fel
// tar även bort ordet om det var rätt svaret och lägger till i wrong containers om man
// svarade fel
pair<bool, string> compare(string userInput, int randomIndex)
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
        return make_pair(false, "Wrong answer! Correct answer is: " + correctAnswer);
    }

    return make_pair(true, "Correct!");
}

// skickar true om man är klar och false om det finns ord kvar att öva på
pair<bool, string> check_empty()
{
    if(phrases_list.empty())
    {
        phrases_list = std::move(wrong_answers);
        translation_list = std::move(wrong_translations);

        if(!phrases_list.empty())
        {
            return make_pair(false, "Practise your mistakes!");
        }
    }
    return make_pair(true, "No more words to practise!");
}

long int random(int max)
{
    random_device r;
    default_random_engine e1(r());
    uniform_int_distribution<int> uniform_dist(0, max);
    
    return uniform_dist(e1);
}

// skickar tillbaka ordet och indexet för att man ska hitta rätt översättning till compare
pair<string, int> pickWord()
{
    string userInput;

    while(!phrases_list.empty())
    {
        long int randomIndex{random(phrases_list.size() - 1)};

        return make_pair(phrases_list.at(randomIndex), randomIndex);
    }
    return pair<string, int>{};
}

bool writeToFile(string const& fileName, string const& contentToWrite)
{
    string filePath = fileName + ".txt";

    // om filen redan finns - skriv inte till den
    if(std::filesystem::exists((filePath)))
    {
        return false;
    }

    ofstream file{fileName + ".txt"};
    file << contentToWrite << endl;
    return true;
}