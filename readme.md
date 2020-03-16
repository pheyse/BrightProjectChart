#BrightProjectChart
Draw nice project charts based on data from a spreadsheet file.

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
 
## Description
 
## Download 
You can download the current version here:
[link](https://github.com/pheyse/BrightProjectChart/tree/master/build/libs)

## Usage
You need to install the Java Runtime.

Running the tool:
```
java -jar <jar-file-name> -i <input-file-name> -o <output-file-name>
```
Example call:
```
java -jar BrightProjectChart-1.0.0-all.jar -i "C:\my-plan.xlsx" -o "C:\charts\\mychart.png"
```



## Example files
### Project files
Example input files can be found [here](https://github.com/pheyse/BrightProjectChart/tree/master/data)

### Chart example
Here's a project chart created by the tool:
![Chart](https://github.com/pheyse/BrightProjectChart/blob/master/examples/example_a.png "Chart")

Here's an example to demonstrate the different settings (with the same project plan)
![Chart](https://github.com/pheyse/BrightProjectChart/blob/master/examples/example_b.png "Chart")

## Input data
The input data must be located in a Excel file (XLSX) with these columns (also see [example input files](https://github.com/pheyse/BrightProjectChart/tree/master/data))
 - type: "setting", "section", "bar" or "milestone"
 - label
 - start: for bar: the beginning of the bar, for milestone: the time of the milestone
 - end: for bar: the end of the bar
 - color: the color in the format red, green, blue (where each value is between 0 and 255)
 - text-size
 - text-pos: empty for default or one of the values "before", "beginning", "center", "end", "after".
 - bold: empty or "FALSE" for false and "TRUE" for yes
 - italic: empty or "FALSE" for false and "TRUE" for yes
 - indent: number of pixels that the label of a section is indented
 - setting: if the type is "setting" the name of the setting is provided here
 - value: if the type is "setting" the value of the setting is provided here
 
 
## Possible settings
All settings are optional.

This example file contains all settings and default the values: [settings example file](https://github.com/pheyse/BrightProjectChart/tree/master/data/Test_B_PP_001.xlsx)