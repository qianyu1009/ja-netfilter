# ja-netfilter v1.1.1

### A javaagent lib for network filter

## Usage

* download from the [releases page](https://github.com/pengzhile/ja-netfilter/releases)
* add `-javaagent:/absolute/path/to/ja-netfilter.jar` argument (**Change to your actual path**)
    * add as an argument of the `java` command. eg: `java -javaagent:/absolute/path/to/ja-netfilter.jar -jar executable_jar_file.jar`
    * some apps support the `JVM Options file`, you can add as a line of the `JVM Options file`.
    * **WARNING: DO NOT put some unnecessary whitespace characters!**

* edit your own rule list config file. The `ja-netfilter` will look for it in the following order(find one and stop searching):
    * passed as args of `-javaagent`. eg: `-javaagent:/absolute/path/to/ja-netfilter.jar=/home/neo/downloads/janf_config.txt`
    * file path in environment variable: `JANF_CONFIG`
    * file path in `java` startup property: `janf.config`. `eg: java -Djanf.config="/home/neo/downloads/janf_config.txt"`
    * some apps support the `JVM Options file`, you can add as a line of the `JVM Options file`. `eg: -Djanf.config="/home/neo/downloads/janf_config.txt"`
    * file path in the same dir as the `ja-netfilter.jar`, no need for additional configuration (<font color=green>**PREFERRED!**</font>)
    * file path in your home directory, named: `.janf_config.txt`. `eg: /home/neo/.janf_config.txt`
    * file path in the subdirectory named `.config` in your home directory. `eg: /home/neo/.config/janf_config.txt`
    * file path in the subdirectory named `.local/etc` in your home directory. `eg: /home/neo/.local/ect/janf_config.txt`
    * file path in the directory named `/usr/local/etc`. `eg: /usr/local/etc/janf_config.txt`
    * file path in the directory named `/etc`. eg: `/etc/janf_config.txt`

* run your java application and enjoy~

## Config file format

```
[ABC]
# for the specified plugin called "ABC"

[URL]
EQUAL,https://someurl

[DNS]
EQUAL,somedomain

# EQUAL     Use `equals` to compare
# KEYWORD   Use `contains` to compare
# PREFIX    Use `startsWith` to compare
# SUFFIX    Use `endsWith` to compare
# REGEXP    Use regular expressions to match
```

## Debug info

* the `ja-netfilter` will **NOT** output debugging information by default
* add environment variable `JANF_DEBUG=1` and start to enable it
* or add system property `-Djanf.debug=1` to enable it

## Plugin system

* for developer:
    * view the [scaffold project](https://github.com/pengzhile/ja-netfilter-sample-plugin) written for the plugin system
    * compile your plugin and publish it
    * just use your imagination~

* for user:
    * download the jar file of the plugin
    * put it in the subdirectory called `plugins` where the ja-netfilter.jar file is located
    * enjoy the new capabilities brought by the plugin
   