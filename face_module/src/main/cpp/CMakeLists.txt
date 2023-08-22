# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Declares and names the project.

project("face")

include_directories(include)

add_library(
        face
        SHARED
        face.cpp
)


find_library(
        log-lib
        log)

add_library(
        opencv
        SHARED
        IMPORTED)

set_target_properties(
        opencv
        PROPERTIES
        IMPORTED_LOCATION
        ${PROJECT_SOURCE_DIR}/libs/${CMAKE_ANDROID_ARCH_ABI}/libopencv_java4.so)

#add_library(
#        ffmpeg
#        SHARED
#        IMPORTED)
#
#set_target_properties(
#        ffmpeg
#        PROPERTIES
#        IMPORTED_LOCATION
#        ${PROJECT_SOURCE_DIR}/libs/${CMAKE_ANDROID_ARCH_ABI}/libffmpeg.so)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${PROJECT_SOURCE_DIR}/libs/${CMAKE_ANDROID_ARCH_ABI}")

target_link_libraries(
        face
        opencv
#        ffmpeg
        ${log-lib}
        jnigraphics
        android
        )

