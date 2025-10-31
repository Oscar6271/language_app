//
// Created by oscar on 10/27/25.
//

#pragma once
#include <vector>
#include <string>

extern std::vector<std::string> wrong_answers;
extern std::vector<std::string> wrong_translations;
extern std::vector<std::string> phrases_list;
extern std::vector<std::string> translation_list;
extern long int randomIndex;

void readFile(std::string const& fileName, std::string const& language_to_write_in);

// skickar true om svaret var rätt, false om det var fel
// tar även bort ordet om det var rätt svaret och lägger till i wrong containers om man
// svarade fel
std::string compare(std::string userInput);

// skickar true om man är klar, annars false
bool check_empty();

// skickar tillbaka ordet
std::string pickWord();

// skickar tillbaka true om man kunde skriva till filen, annars false
void writeToFile(std::string const& fileName,
                 std::string const& contentToWrite, bool append);

std::string printFile(std::string const& fileName);