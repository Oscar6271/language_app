//
// Created by oscar on 6/9/26.
//
#include "../headers/library.h"

#include <algorithm>
#include <cwctype>
#include <iostream>

using namespace std;

void to_lower(string & word)
{
    transform(word.begin(), word.end(), word.begin(),
              [](unsigned char c)
              {
                  return tolower(c);
              });
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

string trim_white_space(string & phrase)
{
    phrase.erase(0, phrase.find_first_not_of(" \t\r\n"));
    phrase.erase(phrase.find_last_not_of(" \t\r\n") + 1);

    clean_string(phrase);
    return phrase;
}

string ignore_explanation(string const& word)
{
    auto start = word.find('(');

    string alternative = word;
    if(start != string::npos)
    {
        alternative = word.substr(0, start);
        trim_white_space(alternative);
        to_lower(alternative);
    }
    return alternative;
}

vector<string> find_alternatives(string translation)
{
    vector<string> alternatives{};
    do
    {
        auto pos = translation.find('/');

        if(pos == string::npos && translation == "")
        {
            break;
        }
        else if(pos == string::npos && translation != "")
        {
            trim_white_space(translation);
            to_lower(translation);
            alternatives.push_back(translation);
            break;
        }


        string alternative = translation.substr(0, pos);
        trim_white_space(alternative);
        to_lower(translation);
        alternatives.push_back(alternative);

        translation = translation.substr(pos + 1);
    } while (true);

    return alternatives;
}

bool end_of_file(size_t pos, int seperator, size_t max_seperator)
{
    return pos == string::npos && seperator == max_seperator;
}