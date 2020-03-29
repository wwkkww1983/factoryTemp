# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

set(distribution_DIR ${CMAKE_SOURCE_DIR}/libs)

add_library(libtempalg1_0 SHARED IMPORTED)
set_target_properties(libtempalg1_0 PROPERTIES IMPORTED_LOCATION
    ${distribution_DIR}/${ANDROID_ABI}/libtempalg1_0.so)

add_library(libtempalg1_5 SHARED IMPORTED)
set_target_properties(libtempalg1_5 PROPERTIES IMPORTED_LOCATION
    ${distribution_DIR}/${ANDROID_ABI}/libtempalg1_5.so)

 # build application's shared lib
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

add_library( # Sets the name of the library.
             tempalgorithm

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/JniHelper.cpp )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.
target_include_directories(tempalgorithm PRIVATE
                           ${distribution_DIR}/include
                           )

target_link_libraries( # Specifies the target library.
                       tempalgorithm
                       android
                       libtempalg1_0
                       libtempalg1_5
                       # Links the target library to the log library
                       # included in the NDK.
                       log )
