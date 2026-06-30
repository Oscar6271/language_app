//
// Created by oscar on 6/9/26.
//

#ifndef ORD_APP_LIBRARY_H
#define ORD_APP_LIBRARY_H

#include <string>
#include <vector>
#include <utility>

void to_lower(std::string & word);
std::string trim_white_space(std::string & word);

std::string ignore_explanation(std::string const& word);

std::vector<std::string> find_alternatives(std::string translations);

bool end_of_file(size_t pos, int seperator, size_t max_seperator);

#endif //ORD_APP_LIBRARY_H
