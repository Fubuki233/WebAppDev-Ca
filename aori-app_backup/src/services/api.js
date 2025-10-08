/*  This is the api.js Service to connect to the rest of the Spring Boot project
    @author Ying Chun
 *  @date 2025-10-07
 *  @version 1.0
 */

import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api', // Your Spring Boot REST base URL
});

export default api;
