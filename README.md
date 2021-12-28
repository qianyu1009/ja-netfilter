# ja-netfilter v2.0.1

### A javaagent framework

## Usage

* download from the [releases page](https://github.com/ja-netfilter/ja-netfilter/releases)
* add `-javaagent:/absolute/path/to/ja-netfilter.jar` argument (**Change to your actual path**)
    * add as an argument of the `java` command. eg: `java -javaagent:/absolute/path/to/ja-netfilter.jar -jar executable_jar_file.jar`
    * some apps support the `JVM Options file`, you can add as a line of the `JVM Options file`.
    * **WARNING: DO NOT put some unnecessary whitespace characters!**

* edit your plugin config files: `${lower plugin name}.conf` file in the `conf` dir where `ja-netfilter.jar` is located.

* run your java application and enjoy~

## Config file format

```
[ABC]
# for the specified section name

# for example
[URL]
EQUAL,https://someurl

[DNS]
EQUAL,somedomain

# EQUAL       Use `equals` to compare
# EQUAL_IC    Use `equals` to compare, ignore case
# KEYWORD     Use `contains` to compare
# KEYWORD_IC  Use `contains` to compare, ignore case
# PREFIX      Use `startsWith` to compare
# PREFIX_IC   Use `startsWith` to compare, ignore case
# SUFFIX      Use `endsWith` to compare
# SUFFIX_IC   Use `endsWith` to compare, ignore case
# REGEXP      Use regular expressions to match
```


## Debug info

* the `ja-netfilter` will **NOT** output debugging information by default
* add environment variable `JANF_DEBUG=1` and start to enable it
* or add system property `-Djanf.debug=1` to enable it

## Plugin system

* for developer:
    * view the [scaffold project](https://github.com/ja-netfilter/ja-netfilter-sample-plugin) written for the plugin system
    * compile your plugin and publish it
    * just use your imagination~

* for user:
    * download the jar file of the plugin
    * put it in the subdirectory called `plugins` where the ja-netfilter.jar file is located
    * enjoy the new capabilities brought by the plugin
   