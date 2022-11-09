# Scribbly-bot

---

A simple light-weight spring boot application which generates png files from text using openai DALL.E 2. <br>
Telegram bot build on top of [Telegram Bot](https://github.com/rubenlagus/TelegramBots) Library

# Requirements

---

* Java 17
* Gradle <br>


build command
```bash
./gradlew BootJar
```

* Docker

```bash
docker build -t scribbly:latest .
```


Docker container

```bash
docker run --name scribbly -rm -it -p 8080:8080 scribbly:latest
```


# Configuring the bot tokens

---

Change the configuration in spring-boot application.yml file

```yaml
secrets:
  telegram-bot: bot-token
  api-key: api key
```



