#include <fstream>
#include <iostream>
#include <algorithm>
#include <cstdlib>
#include <limits>
#include <iomanip>
#include <random>
#include <cstdio>

#include "../headers/setup.h"

using namespace std;

std::vector<std::string> wrong_answers;
std::vector<std::string> wrong_translations;
std::vector<std::string> phrases_list;
std::vector<std::string> translation_list;
long int randomIndex;

void clear_lists()
{
    wrong_translations.clear();
    wrong_answers.clear();
    phrases_list.clear();
    translation_list.clear();
}

void readFile(string const& fileName, string const& language_to_write_in)
{
    ifstream file{fileName + ".txt"};
    string line;
    clear_lists();

    while(getline(file, line))
    {
        auto pos = line.find(',');
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

            trim_white_space(phrase);
            trim_white_space(translation);

            phrases_list.push_back(phrase);
            translation_list.push_back(translation);
        }
    }
    file.close();
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
            return "Correct!";
        }
    }

    wrong_answers.push_back(phrase);
    wrong_translations.push_back(fullAnswer);
    return "Wrong, " + phrase + " means " + fullAnswer;
}

// skickar true om man är klar, annars false
bool check_empty()
{
    if(phrases_list.empty())
    {
        phrases_list = std::move(wrong_answers);
        translation_list = std::move(wrong_translations);
    }

    return phrases_list.empty();
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

        return phrases_list.at(randomIndex);
    }
    return string{};
}

void writeToFile(string const& fileName,
                 string const& contentToWrite, bool append)
{
    ofstream file;

    if(append)
    {
        file.open(fileName + ".txt", ios::app);
    }
    else
    {
        file.open(fileName + ".txt");
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