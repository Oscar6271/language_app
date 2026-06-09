//
// Created by oscar on 6/9/26.
//

#ifndef ORD_APP_LIBRARY_H
#define ORD_APP_LIBRARY_H

#include <string>
#include <vector>
#include <utility>

void to_lower(std::string & word);
void trim_white_space(std::string & word);

std::string ignore_explanation(std::string const& word);

std::vector<std::string> find_alternatives(std::string translations);

#endif //ORD_APP_LIBRARY_H
