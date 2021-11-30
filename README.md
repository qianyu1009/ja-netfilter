# ja-netfilter

### A javaagent lib for network filter

## Usage

* download from the [releases page](https://github.com/pengzhile/ja-netfilter/releases)
* add `-javaagent:/absolute/path/to/ja-netfilter.jar` argument (**Change to your actual path**)
    * add as an argument of the `java` command.
      eg: `java -javaagent:/absolute/path/to/ja-netfilter.jar -jar executable_jar_file.jar`
    * some apps support the `JVM Options file`, you can add as a line of the `JVM Options file`.
    * **WARNING: DO NOT put some unnecessary whitespace characters!**

* edit your own rule list config file. The `ja-netfilter` will look for it in the following order(find one and stop
  searching):
    * passed as args of `-javaagent`.
      eg: `-javaagent:/absolute/path/to/ja-netfilter.jar=/home/neo/downloads/janf_config.txt`
    * file path in environment variable: `JANF_CONFIG`
    * file path in `java` startup property: `janf.config`
      . `eg: java -Djanf.config="/home/neo/downloads/janf_config.txt"`
    * some apps support the `JVM Options file`, you can add as a line of the `JVM Options file`
      . `eg: -Djanf.config="/home/neo/downloads/janf_config.txt"`
    * file path in the same dir as the `ja-netfilter.jar` (**PREFERRED!**)
    * file path in your home directory, named: `.janf_config.txt`. `eg: /home/neo/.janf_config.txt`
    * file path in the subdirectory named `.config` in your home directory. `eg: /home/neo/.config/janf_config.txt`
    * file path in the subdirectory named `.local/etc` in your home
      directory. `eg: /home/neo/.local/ect/janf_config.txt`
    * file path in the directory named `/usr/local/etc`. `eg: /usr/local/etc/janf_config.txt`
    * file path in the directory named `/etc`. eg: `/etc/janf_config.txt`

* run your java application and enjoy~

## Config file format

```
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