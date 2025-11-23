# **Overview**

The Transtalk API provides user authentication and management, along with real-time messaging functionalities. Users can view their profiles, create chat rooms, and exchange real-time messages with other users through a secure authentication process.

# **Base Information**

- **REST API Base URL**: `https://transtalk.duckdns.org`
- **WebSocket URL**: `wss://transtalk.duckdns.org/ws-connect`
- **Authentication**: JWT (using Access Token)
- **Data Format**: JSON

# **Endpoints**

### **1. Authentication and User Management**

| **Feature** | **Method** | **URL** | **Access Token Required** | **Status Codes** |
| --- | --- | --- | --- | --- |
| Get Authorization Code | `GET` | `/api/v1/auth` | X | `200 OK`, `400 BadRequest` |
| Sign Up and Sign In | `GET` | `/api/v1/auth/token?code={code}` | X | `200 OK`, `400 BadRequest`, `401 Unauthorized`, `404 Not Found`, `406 Not Acceptable` |
| Reissue Access Token | `GET` | `/api/v1/auth/refresh` | X | `200 OK`, `400 BadRequest`, `404 Not Found`, `406 Not Acceptable` |
| Get My Profile | `GET` | `/api/v1/me` | O | `200 OK`, `400 BadRequest`, `401 Unauthorized`, `404 Not Found`, `406 Not Acceptable` |
| Logout | `POST` | `/api/v1/auth/logout` | O | `200 OK`, `400 BadRequest`, `401 Unauthorized`, `404 Not Found`, `406 Not Acceptable` |
| Withdraw Member | `DELETE` | `/api/v1/auth/withdraw` | O | `200 OK`, `404 Not Found`, `406 Not Acceptable` |

---

### **(1) Get Authorization Code (`GET /api/v1/auth`)**

- **Feature Description**: This is the first step in the Google login flow. It requests a URL for the Google login page. The client redirects to this URL to proceed with user authentication and obtain an authorization code (`code`).
- **Request**: None (called directly by the client).
- **Response (Success)**: The client redirects to the URL provided in the `data` field. After redirection, the `code` value can be obtained from the browser's query parameters. This `code` value is then used as a query parameter for the `GET /api/v1/auth/token` method.
    - **Example `code` value**: `4%2F0Ab32j90_Q6D9sW2M44QWrO0PHBeYKuFjpN9suPfUxNTUqget94f4A4TMkk4tav7LLwH7eA`

    ```json
    {
        "success": true,
        "message": "Successfully requested authorization code URL.",
        "data": "https://accounts.google.com/o/oauth2/v2/auth?client_id=1059391415473-k63eiucuo8rqopaot5jh0ecn35hghdra.apps.googleusercontent.coms&redirect_uri=http://localhost:8089/api/v1/auth&response_type=code&scope=email profile&access_type=offline",
        "timestamp": "2025-11-08T17:12:09.6313447",
        "errorCode": null
    }
    
    ```

- **Response (Failure)**: For failure responses, additional configuration for `errorCode` might be needed.

    ```json
    {
        "success": false,
        "message": "Failed to request authorization code URL.",
        "data": "https://accounts.google.com/o/oauth2/v2/auth?client_id=1059391415473-k63eiucuo8rqopaot5jh0ecn35hghdra.apps.googleusercontent.coms&redirect_uri=http://localhost:8089/api/v1/auth&response_type=code&scope=email profile&access_type=offline",
        "timestamp": "2025-11-08T17:12:09.6313447",
        "errorCode": null
    }
    
    ```


---

### **(2) Sign Up and Sign In (`GET /api/v1/auth/token?code={code}`)**

- **Feature Description**: Uses the authorization code (`code`) received from Google to retrieve user information and process user sign-up or sign-in for the Transtalk service.
- **Notes**:
    - The `code` value is one-time use. If login/sign-up fails, the authorization code must be obtained again (re-execute from method 1).
    - The `TokenResponse` includes `accessToken` and `refreshToken` values, but for security, the `refreshToken` is not included in the response body directly; it is stored in a cookie.
    - For all subsequent requests requiring authentication, the `accessToken` received in the response must be included in the `Authorization` header in the format `Bearer {AccessToken}`.
    - The `refreshToken` is managed by storing it in a cookie on the client-side.
- **Request**: `code` (query parameter)

    ```
    GET /api/v1/auth/token?code=YOUR_AUTHORIZATION_CODE
    
    ```

- **Response (Success)**: An `AuthSignInResponse` object is returned in the `data` field, containing `userResponse` (user information) and `tokenresponse` (token information).
    - `userResponse`: `id`, `email`, `name`, `picture`
    - `tokenresponse`: `accessToken`, `refreshToken`

    ```json
    {
        "success": true,
        "message": "Authentication request successful.",
        "data": {
            "userResponse": {
                "id": 1,
                "email": "lakevely27@gmail.com",
                "name": "이호수",
                "picture": "https://lh3.googleusercontent.com/a/ACg8ocJ-8efaKTDZAOQm3Ld8eGD-z2rdDipP5grY0hzFaiU4SlWfew=s96-c"
            },
            "tokenresponse": {
                "accessToken": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZW1haWwiOiJsYWtldmVseTI3QGdtYWlsLmNvbSIsIm5hbWUiOiLsnbTtmLjsiJgiLCJ1c2VyUm9sZSI6IlJPTEVfVVNFUiIsImV4cCI6MTc2Mjc3NTQ5NiwiaWF0IjoxNzYyNzc0ODk2fQ.TAxLwLZuNC_js6xXI__y6cl1LQMCA7AHfjV8f-H6ADE",
                "refreshToken": "378eb933-7cb7-46d7-968e-bf6c9ccbc51e"
            }
        },
        "timestamp": "2025-11-10T20:41:36.843398",
        "errorCode": null
    }
    
    ```

- **Response (Failure)**:

    ```json
    {
        "success": false,
        "message": null,
        "data": null,
        "timestamp": "2025-11-10T20:41:36.843398",
        "errorCode": null
    }
    
    ```


---

### **(3) Reissue Access Token (`GET /api/v1/auth/refresh`)**

- **Feature Description**: Uses the Refresh Token stored in a cookie to re-issue a new Access Token when the current Access Token has expired.
- **Request**: None (automatically uses the Refresh Token from the client's cookies).
- **Error Cases**:
    - `404 Not Found`: If no user information corresponds to the Refresh Token.
    - `400 Bad Request`: If there are input errors such as typos in the request.
    - `406 Not Acceptable`: If the Refresh Token has expired.
- **Response (Success)**: A new `accessToken` and `refreshToken` are returned. The `refreshToken` may be a new value or `null`.

    ```json
    {
        "success": true,
        "message": null,
        "data": {
            "accessToken": "Bearer eyJhbGciOiJIUzI1NiJ9.5MDIyOSwiaWFOIjoxNzYyNTg5NjI5fQ.JctNbh8nGFMi6bD2SUDlwcPUDUxxkw3QnGDUk4BktZc",
            "refreshToken": null
        },
        "timestamp": "2025-11-08T17:13:49.8025353",
        "errorCode": null
    }
    
    ```

- **Response (Failure)**:

    ```json
    {
        "success": false,
        "message": null,
        "data": null,
        "timestamp": "2025-11-08T17:13:49.8025353",
        "errorCode": null
    }
    
    ```


---

### **(4) Get My Profile (`GET /api/v1/me`)**

- **Feature Description**: Retrieves the profile information of the currently logged-in user. Requires a valid Access Token in the `Authorization` header.
- **Request**: None (only a valid Access Token is required).
- **Error Cases**:
    - `404 Not Found`: If no user information corresponds to the Access Token.
    - `400 Bad Request`: If there are input errors such as typos in the request.
    - `401 Unauthorized`: If the user is not logged in (Access Token is missing or invalid).
    - `406 Not Acceptable`: If the Access Token has expired (re-issuance needed).
- **Response (Success)**:

    ```json
    {
        "success": true,
        "message": "User information retrieved successfully.",
        "data": {
            "id": 1,
            "email": "lakevely27@gmail.com",
            "name": "이호수",
            "picture": "https://lh3.googleusercontent.com/a/ACg8ocJ-8efaKTDZAOQm3Ld8eGD-z2rdDipP5grY0hzFaiU4SlWfew=s96-c"
        },
        "timestamp": "2025-11-12T23:30:56.1295872",
        "errorCode": null
    }
    
    ```

- **Response (Failure)**:

    ```json
    {
        "success": false,
        "message": null,
        "data": null,
        "timestamp": "2025-11-12T23:30:56.1295872",
        "errorCode": null
    }
    
    ```


---

### **(5) Logout (`POST /api/v1/auth/logout`)**

- **Feature Description**: Terminates the current user's session and invalidates token information, including blacklisting the Access Token for immediate invalidation.
- **Logout Flow**:
    1. Retrieve `userId` and `accessToken` (from Authorization header) and `refreshToken` (from cookie).
        - If the Access Token is valid, extract `userId` from it.
        - If the Access Token is expired, use the Refresh Token to obtain `userId`.
    2. **Add the current `accessToken` to a Redis-based blacklist. This ensures that the Access Token becomes immediately invalid, preventing its further use even if it hasn't naturally expired yet.**
    3. Remove the `refreshToken` stored in Redis that corresponds to the `userId` and `refreshToken` value.
    4. Set the client-side cookie value for the `refreshToken` to `null` or expire it.
- **Request**: None (only a valid Access Token is required).
- **Response (Success)**: Returns only `200 OK` status code without a response body.
- **Response (Failure)**:

    ```json
    {
        "success": false,
        "message": null,
        "data": null,
        "timestamp": "2025-11-12T23:30:56.1295872",
        "errorCode": null
    }
    
    ```


---

### **(6) Withdraw Member (`DELETE /api/v1/auth/withdraw`)**

- **Feature Description**: Processes the withdrawal of the currently logged-in user's account. User data is handled according to relevant policies.
- **Withdrawal Flow**:
    1. Obtain the Access Token value from the request header.
        - If the Access Token is missing, an `IllegalArgumentException` is thrown.
    2. Obtain the `userId` value from the Access Token.
    3. Retrieve the user to be deleted using the `userId` value.
        - If no corresponding user is found, a `404 Not Found` error is thrown.
    4. Remove the user's Refresh Token from Redis.
    5. Perform a soft delete of the user. (The `deletedAt` field of the User entity is set to the current time).
    6. Proceed with the Transtalk account withdrawal linked to Google using the Google API and Access Token.
    7. Publish a user withdrawal event to soft delete all related RDB data (Participant, ChatRoom, Chat).
    8. Set the Refresh Token cookie value on the client-side to `null`.
- **Request**: None (only a valid Access Token is required).
- **Response (Success)**:

    ```json
    {
        "success": true,
        "message": "Member withdrawal successful.",
        "data": null,
        "timestamp": "2025-11-12T23:30:56.1295872",
        "errorCode": null
    }
    
    ```

- **Response (Failure)**:

    ```json
    {
        "success": false,
        "message": null,
        "data": null,
        "timestamp": "2025-11-12T23:30:56.1295872",
        "errorCode": null
    }
    
    ```


---

### **2. Chat Rooms and Messages**

| **Feature** | **Method** | **URL** | **Access Token Required** | **Status Codes** |
| --- | --- | --- | --- | --- |
| View Chat History | `GET` | `/api/v1/chatRooms/{chatRoomId}/chats` | O | `200 OK`, `404 Not Found` |
| Get Chat Room List | `GET` | `/api/v1/chatRooms?page={page}&name={name}` | O | `200 OK`, `404 Not Found` |
| Create Chat Room | `POST` | `/api/v1/chatRooms` | O | `200 OK`, `400 BadRequest`, `404 Not Found` |

---

### **(7) View Chat History (`GET /api/v1/chatRooms/{chatRoomId}/chats`)**

- **Feature Description**: Retrieves previous message history for a specific chat room. Messages include translated content, sender information, send time, read status, and message processing status.
- **Request**:
    - Path Parameter: `{chatRoomId}` (ID of the chat room to retrieve).
    - Query Parameter: Paging related parameters (e.g., `page`, `size`) might be required.
- **Message `status`**:
    - `PENDING`: Message in processing (e.g., translation pending).
    - `COMPLETED`: Message processing complete (e.g., translation finished).
    - `FAILED`: Message processing failed.
- **Response (Success)**:

    ```json
    {
        "success": true,
        "message": null,
        "data": {
            "chats": [
                {
                    "chatId": 6,
                    "originalMessage": "nice to meet you,5",
                    "translatedMessage": "만나서 반갑습니다.5",
                    "senderEmail": "user1@Email",
                    "sendAt": "2025-11-16T16:17:27.685163",
                    "isRead": true,
                    "status": "COMPLETED"
                },
                {
                    "chatId": 5,
                    "originalMessage": "nice to meet you,5",
                    "translatedMessage": "만나서 반갑습니다.5",
                    "senderEmail": "user1@Email",
                    "sendAt": "2025-11-16T16:17:26.137782",
                    "isRead": true,
                    "status": "COMPLETED"
                },
                {
                    "chatId": 4,
                    "originalMessage": "hello4",
                    "translatedMessage": "안녕4",
                    "senderEmail": "user2@Email",
                    "sendAt": "2025-11-16T16:17:21.82914",
                    "isRead": true,
                    "status": "COMPLETED"
                },
                {
                    "chatId": 3,
                    "originalMessage": "hello2",
                    "translatedMessage": "안녕2",
                    "senderEmail": "user2@Email",
                    "sendAt": "2025-11-16T16:17:18.98088",
                    "isRead": true,
                    "status": "COMPLETED"
                },
                {
                    "chatId": 2,
                    "originalMessage": "hello",
                    "translatedMessage": "안녕",
                    "senderEmail": "user2@Email",
                    "sendAt": "2025-11-16T16:17:16.253038",
                    "isRead": true,
                    "status": "COMPLETED"
                },
                {
                    "chatId": 1,
                    "originalMessage": "nice to meet you",
                    "translatedMessage": "만나서 반갑습니다",
                    "senderEmail": "user1@Email",
                    "sendAt": "2025-11-16T16:17:13.999869",
                    "isRead": true,
                    "status": "COMPLETED"
                }
            ],
            "pageNumber": 0,
            "pageSize": 40,
            "hasNext": false,
            "isLast": true,
            "totalElements": 6,
            "recipient": {
                "recipientPicture": "https://lh3.googleusercontent.com/a/ACg8ocKJ_lJ",
                "recipientEmail": "user1@Email",
                "recipientName": "user1"
            }
        },
        "timestamp": "2025-11-16T16:17:32.420023",
        "errorCode": null
    }
    
    ```

- **Response (Failure)**:

    ```json
    {
        "success": false,
        "message": "Failed to retrieve chat history.",
        "data": null,
        "timestamp": "2025-11-16T16:17:32.420023",
        "errorCode": "..."
    }
    
    ```


---

### **(8) Get Chat Room List (`GET /api/v1/chatRooms?page={page}&name={name}`)**

- **Feature Description**: Retrieves a list of chat rooms that the current user belongs to from the main screen. Member information from the user's JWT token is used to identify their chat rooms.
- **Paging**: Up to 20 data points can be displayed on a single page.
- **Search**: If a `name` query parameter is provided, chat rooms can be searched using the other user's name.
- **Request**:
    - Query Parameters:
        - `page`: The page number to retrieve (e.g., `0`, `1`, `2`...). (Default is expected to be 0).
        - `name`: The name of the recipient user to search for (optional).

    ```
    GET /api/v1/chatRooms?page=0&name=test1
    
    ```

- **Error Cases**:
    - `404 Not Found`: If the chat room participant information lookup fails (for current user's info or other user's info).
- **Output Requirements**:
    - If a chat room has been created but no messages have been sent, `originalRecentMessage` and `translatedRecentMessage` will return empty strings (`""`).
    - `recentMessageTime` will return `null`.
- **Response (Success)**:

    ```json
    {
        "success" : true,
        "message" : null,
        "data" : {
            "rooms" : [
                {
                    "chatroomId" : 2,
                    "recipientPicture" : "https://lh3.googleusercontent.com/a/ACg8ocKJ_lJKVh7nej_K3JvOdyuiQPJd4Y8PlhuF9DP4NrKbt6De1Q=s96-c",
                    "recipientName" : "bong",
                    "selectedLanguage" : "ko",
                    "originalRecentMessage" : "hello",
                    "translatedRecentMessage" : "안녕",
                    "recentMessageTime" : "2025-11-13T05:13:55.562084Z",
                    "unreadMessageCount" : 0
                },
                {
                    "chatroomId" : 1,
                    "recipientPicture" : "https://lh3.googleusercontent.com/a/ACg8ocKJ_lJKVh7nej_K3JvOdyuiQ",
                    "recipientName" : "seon",
                    "selectedLanguage" : "ko",
                    "originalRecentMessage" : "bye",
                    "translatedRecentMessage" : "잘가",
                    "recentMessageTime" : "2025-11-13T05:13:55.560848Z",
                    "unreadMessageCount" : 0
                }
            ],
            "pageNumber" : 0,
            "pageSize" : 20,
            "hasNext" : false,
            "isLast" : true,
            "totalElements" : 2
        },
        "timestamp" : "2025-11-13T14:13:55.619852",
        "errorCode" : null
    }
    
    ```

- **Response (Failure)**:

    ```json
    {
        "success": false,
        "message": "Failed to retrieve chat room participant information.",
        "data": null,
        "timestamp": "2025-11-12T01:17:34.927664",
        "errorCode": null
    }
    
    ```


---

### **(9) Create Chat Room (`POST /api/v1/chatRooms`)**

- **Feature Description**: Creates a new chat room. A chat room can be created by specifying the recipient's email and the preferred language.
- **Available Language Codes**: `[ko]`, `[es]`, `[ja]`, `[en-us]`, `[zh]`
- **Request**:

    ```json
    {
        "language":"ko",
        "recipientEmail":"recipient@naver.com"
    }
    
    ```

- **Response (Success)**:

    ```json
    {
        "success": true,
        "message": null,
        "data": {
            "chatRoomId": 1
        },
        "timestamp": "2025-11-12T00:38:34.634119",
        "errorCode": null
    }
    
    ```

- **Response (Failure)**:
    - `404 Not Found`: If a member cannot be found via email.

        ```json
        {
            "success":false,
            "message": "Failed to retrieve user information.",
            "data": null,
            "timestamp": "2025-11-12T00:38:34.634119",
            "errorCode": 404
        }
        
        ```

    - `400 Bad Request`: If the email format is incorrect.

        ```json
        {
            "success": false,
            "message": "Incorrect email format.",
            "data": null,
            "timestamp": "2025-11-13T12:40:40.162477",
            "errorCode": "400"
        }
        
        ```


---

### **3. WebSocket**

Transtalk uses STOMP over WebSocket for real-time communication.

| **Feature** | **Method** | **Target URL** | **Access Token Required** |
| --- | --- | --- | --- |
| WebSocket Connection | `CONNECT` | `ws://transtalk.duckdns.org/ws-connect` | O |
| Subscribe Private Message | `SUBSCRIBE` | `/topic/chat/{chatRoomId}` | O |
| Unsubscribe Private Message | `UNSUBSCRIBE` | `/topic/chat/{chatRoomId}` | O |
| Send a Message | `SEND` | `/app/chat/{chatRoomId}` | O |
| Subscribe Chat Room List | `SUBSCRIBE` | `/topic/users/{userId}/chatRoom` | O |
| Unsubscribe Chat Room List | `UNSUBSCRIBE` | `/topic/users/{userId}/chatRoom` | O |
| New Message Notification (Received) | `MESSAGE` | `/topic/chat/{chatRoomId}` (received from server) | X |
| New Message in Chat Room List (Received) | `MESSAGE` | `/topic/users/{userId}/chatRoom` (received from server) | X |

---

### **(10) WebSocket Connection (`CONNECT ws://transtalk.duckdns.org/ws-connect`)**

- **Feature Description**: This is a WebSocket connection request. After logging in, a WebSocket connection request can be sent from the main screen displaying the chat room list.
- **Notes**: The WebSocket connection will not be terminated unless the website is closed.

---

### **(11) Subscribe Private Message (`SUBSCRIBE /topic/chat/{chatRoomId}`)**

- **Feature Description**: When entering a chat room, you can subscribe to messages for that chat room using the `chatRoomId`. By subscribing, you can receive new messages sent to that chat room in real-time.

---

### **(12) Unsubscribe Private Message (`UNSUBSCRIBE /topic/chat/{chatRoomId}`)**

- **Feature Description**: When you finish chatting and leave a chat room, you unsubscribe from messages in that chat room. This stops you from receiving further messages for that chat room.

---

### **(13) Send a Message (`SEND /app/chat/{chatRoomId}`)**

- **Feature Description**: This is the logic for sending messages in a private chat room. When a sender sends a message to the server, the server delivers the message to the recipients subscribed to that chat room. Upon sending, the server translates the message and stores it in the database.
- **Request**:

    ```json
    {
        "content": "nice to meet you"
    }
    
    ```


---

### **(14) New Message Notification (Received) (`MESSAGE /topic/chat/{chatRoomId}`)**

- **Feature Description**: Messages are delivered to users who have subscribed to this chat room. The message status changes depending on the translation status.
- **Message `status`**:
    - `PENDING`: Translation not completed.
    - `COMPLETED`: Translation completed.
    - `FAILED`: Translation failed.
- **Response**:

    ```json
    {
        "chatId":1,
        "originalMessage":"nice to meet you",
        "translatedMessage":"만나서 반갑습니다",
        "senderEmail":"user@email",
        "sendAt":"2025-11-13T10:27:09.946646Z",
        "isRead":false,
        "status":"COMPLETED"
    }
    
    ```


---

### **(15) Subscribe Chat Room List Notification (`SUBSCRIBE /topic/users/{userId}/chatRoom`)**

- **Feature Description**: This subscription is for receiving notifications when new messages arrive.
    - After logging in, subscribe to this channel when displaying the chat room list on the main screen.
    - Maintain the subscription for the chat room list even after leaving a chat room to receive new message notifications.

---

### **(16) Unsubscribe Chat Room List Notification (`UNSUBSCRIBE /topic/users/{userId}/chatRoom`)**

- **Feature Description**: Unsubscribe from the chat room list notification when entering a chat room. (To focus on the current chat room).

---

### **(17) New Message in Chat Room List (Received) (`MESSAGE /topic/users/{userId}/chatRoom`)**

- **Feature Description**: This message is received when subscribing to the chat room list and a new message occurs in a chat room. When a new message arrives in a chat room, information is provided to update the relevant chat room details.
- **Client Actions**:
    1. If a new `chatRoomId` appears in the chat room list, add the new information to the existing chat list.
    2. If the `chatRoomId` already exists in the chat room list, update the information for that chat room (e.g., latest message, unread message count, etc.).

---

# **Error Handling**

All API responses indicate the processing result via a `status code`. `4xx` (client errors) and `5xx` (server errors) codes are common. In addition to specific error situations described for each endpoint, a general error response format is as follows:

**json**

```json
{
  "timestamp": "2025-11-22T17:10:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid request data.",
  "path": "/api/v1/chatRooms"
}
```

## **Usage Examples (CURL)**

### **1. Get Authorization Code**

```bash
curl -X GET "http://localhost:8080/api/v1/auth"
```

### **2. Sign Up and Sign In (Issue Access/Refresh Token)**

(The authorization code `{code}` is obtained from `GET /api/v1/auth`)

```bash
curl -X GET "http://localhost:8080/api/v1/auth/token?code=YOUR_AUTHORIZATION_CODE"
```

### **3. Get My Profile**

(The authenticated `AccessToken` is included as a `Bearer` token in the `Authorization` header)

```bash
curl -X GET "http://localhost:8080/api/v1/me" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### **4. Logout**

```bash
curl -X POST "http://localhost:8080/api/v1/auth/logout" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### **5. Withdraw Member**

```bash
curl -X DELETE "http://localhost:8080/api/v1/auth/withdraw" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### **6. Create Chat Room**

```bash
curl -X POST "http://localhost:8080/api/v1/chatRooms" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
        "language": "ko",
        "recipientEmail": "recipient@naver.com"
      }'
```

**7. View Chat History**

```bash
curl -X GET "http://localhost:8080/api/v1/chatRooms/your_chat_room_id/chats" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**8. Get Chat Room List**

```bash
curl -X GET "http://localhost:8080/api/v1/chatRooms?page=0&name=test1" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

---