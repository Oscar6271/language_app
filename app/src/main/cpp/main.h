//
// Created by oscar on 10/27/25.
//

#pragma once
#include <vector>
#include <string>

extern std::vector<std::string> wrong_answers, wrong_translations, phrases_list, translation_list;

// skickar tillbaka listorna ifall det behövs
std::pair<std::vector<std::string>, std::vector<std::string>> readFile(std::string const& fileName, std::string const& language_to_write_in);

// skickar true om svaret var rätt, false om det var fel
// tar även bort ordet om det var rätt svaret och lägger till i wrong containers om man
// svarade fel
std::pair<bool, std::string> compare(std::string userInput, int randomIndex);

// skickar true om man är klar och false om det finns ord kvar att öva på
std::pair<bool, std::string> check_empty();

// skickar tillbaka ordet och indexet för att man ska hitta rätt översättning till compare
std::pair<std::string, int> pickWord();

// skickar tillbaka true om man kunde skriva till filen, annars false
bool writeToFile(std::string const& fileName, std::string const& contentToWrite);