# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

add_library(overflow SHARED src/main/cpp/overflow.c)
add_library(HelloWorld SHARED src/main/cpp/HelloWorld.c)
add_library(exceptionThrow SHARED src/main/cpp/exceptionThrow.c)

find_library(log-lib log)
target_link_libraries(HelloWorld ${log-lib})
target_link_libraries(overflow ${log-lib})
target_link_libraries(exceptionThrow ${log-lib})