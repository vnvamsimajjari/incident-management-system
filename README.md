# Incident Management System

## Overview

The Incident Management System is a backend application built using Spring Boot to manage incidents, comments, and notifications with secure authentication.

It provides RESTful APIs for creating, tracking, and resolving incidents across teams.

---

## Tech Stack

Component        : Technology
Language         : Java
Framework        : Spring Boot
Database         : MySQL
Authentication   : JWT (JSON Web Token)

---

## Features

- JWT-based secure authentication for all endpoints
- Full Incident Management with CRUD operations
- Comment Module for adding and retrieving incident comments
- Email Notification Service for updates
- Audit Logging with timestamps and user tracking

---

## Comment Module

The Comment Module allows users to add and retrieve comments linked to specific incidents using incidentId.

---

### Add Comment

POST /api/comments

Request Body:
{
"incidentId": 7,
"comment": "Second test comment"
}

Fields:
incidentId  : Integer  : ID of the incident
comment     : String   : Comment message

---

### Get Comments

GET /api/comments/incident/{incidentId}

Sample Response:
[
{
"id": 1,
"message": "audit restarted successfully",
"createdAt": "2026-03-22T16:55:33",
"username": "admin"
}
]

---

## Sample Database Data

ID   Message                        Incident ID   User ID
1    audit restarted successfully   7             1
2    Working fine                   7             1
3    Working fine                   7             1
4    Working fine                   7             1
5    Second test comment            7             1

---

## Authentication

All endpoints are secured using JWT authentication.

Authorization: Bearer <your_jwt_token>

---

## Project Status

JWT Authentication             : Completed  : All endpoints secured
Incident Management (CRUD)     : Completed  : Full lifecycle support
Comment Module                 : Completed  : Add and Fetch APIs live
Email Notification Service     : Completed  : Alerts on updates
Audit Logging                  : Completed  : Timestamped logs
Incident Escalation Module     : In Progress: Next milestone

---

## Next Steps

- Implement Incident Escalation Module
- Define escalation rules and thresholds
- Add escalation notifications via email
- Write unit and integration tests
- Document escalation APIs

---

## Version

Version 1.0
March 2026

---

## Notes

This document represents the current implementation status where the Comment Module is fully completed and the Escalation Module is under development.