# Google Scholar Author Search - Hybrid MVC Java App

## Overview
A **Java desktop application** to search academic authors and their articles using **SerpApi**. Supports **hybrid search**: by `author_id` (exact) or `author name` (approximate). Implements **MVC pattern** for clear separation of concerns.

---

## Structure
<img width="722" height="243" alt="image" src="https://github.com/user-attachments/assets/71bef5ec-ccca-46bb-98aa-414e01098bcd" />
---

## Features

- Hybrid search: `author_id` or `author name`
- Asynchronous searches with **threading**
- Displays articles in a **table** (Title, Authors, Publication, Year, Cited By, Link)
- Error handling and status messages

---

## Technologies Used

- **Java 17 (Oracle SDK)**
- **Apache HttpClient 5.x**
- **Jackson Databind**
- **Swing GUI**
- **SerpApi** for Google Scholar

