## Functional Requirementes

- [ ] Compose message
- [ ] Send message
- [ ] View my messages
- [ ] Folders/labels - (inbox, sent...)
- [ ] Reply, reply all
- [ ] View a single message

## Non functional requirements

- [ ] High availability
- [ ] High scalability
- [ ] Authentication and authorization

## Pages 

- [ ] Login
- [ ] Home (sidebar with folders, and messages)
- [ ] Message view
- [ ] Message editing

## Tech Stack

- Spring Boot
- Thymeleaf
- Spring Security
- Apache Cassandra


## Arquiteture

```
         html   ___backend____              Dist DB
client <------> | springboot | <-------> Apache Cassandra
                | Thymeleaf  |
                | spring Sec.| --> GitHub oauth
                |____________|
```

maybe a loadbalancer between the backend and the clients.


## Fisical Data model

**FOLDERS_BY_USER**
|Column | Type  | key/cluster|
| ----- | ----- | -          |
|userId | Text  | K          |
|label  |Text   | C          |
|color  |Text   |            |


**UNREAD_EMAIL_STATS**
|Column      | Type   | key/cluster|
| -----      | -----  | -          |
|userId      | Text   | K          |
|label       | Text   | C          |
|num_unread  | Counter|            |


**EMAILS_BY_USER_FOLDER**
|Column   | Type    | key/cluster|
| -----   | -----   | -          |
|userId   |Text    | K          |
|label    |Text     | C          |
|id       |Timeuuid | C          |
|to       |Text     |            |
|subject  |Text     |            |
|is_read  |bool     |            |


**MESSAGES**
|Column | Type      | key/cluster|
| ----- | -----     | -          |
|id     |Timeuuid   | K          |
|from   |Text       |            |
|to     |List<Text> |            |
|body   |text       |            |

*Set up a cassandra db using the free tier of [astra](https://astra.datastax.com).
Create database and create token!
