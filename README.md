# SnapServe

## Project Overview

SnapServe is a user-friendly platform designed to seamlessly connect customers with verified and trusted specialists across a wide range of industries, including home repairs, cleaning, tutoring, and personal care. Our mission is to simplify the process of finding reliable and available professionals, ensuring a trustworthy and efficient experience for both customers and service providers.

## Key Features

*   **Verified Professionals:** SnapServe thoroughly vets and validates all service providers to ensure top-quality work and reliability.
*   **On-Demand Booking:** Customers can quickly book appointments with available service providers, often for the same day.
*   **Responsive Support:** A dedicated customer service team is available to assist with any questions or concerns, standing behind the work of its service providers with a satisfaction guarantee.
*   **Intuitive 4-Step Process:**
    1.  **Search:** Easily search for desired services and locations.
    2.  **Select:** Review provider profiles, ratings, and availability to choose the best fit.
    3.  **Book:** Securely book and pay for services directly through the SnapServe platform.
    4.  **Pay & Rate:** After service completion, securely pay and leave feedback to help others make informed choices.

## High-Level Architecture

SnapServe is built on a robust microservices architecture, ensuring scalability, maintainability, and independent deployment of components.

*   **Frontend:** Developed with React.js and styled using Tailwind CSS, providing a modern and responsive user interface.
*   **Backend:** Comprises multiple Spring Boot microservices, each handling specific domain logic:
    *   `admin-service`
    *   `auth-service`
    *   `booking-service`
    *   `complaint-service`
    *   `config-service`
    *   `customer-service`
    *   `gateway-service`
    *   `notification-service`
    *   `registry-service` (Eureka Server for Service Discovery)
    *   `review-service`
    *   `specialist-service`
*   **Database:** MongoDB Atlas for flexible and scalable data storage.
*   **Security:** Implemented using Spring Security with JWT (JSON Web Tokens) for secure authentication and authorization.
*   **Service Discovery & Configuration:** Utilizes Eureka for service registration and discovery, and Spring Cloud Config for centralized configuration management.

## Technologies Used

*   **Frontend:** React.js, Vite, Tailwind CSS, React Router DOM
*   **Backend:** Java 17, Spring Boot 3.3.x, Spring Cloud (Eureka, Config), Project Reactor (WebFlux), Lombok, JWT
*   **Database:** MongoDB
*   **Build Tools:** Gradle (for backend), npm/pnpm (for frontend)
*   **Other:** Git

## Getting Started

Detailed instructions on how to set up the development environment, build, and run the application will be provided here.

## Future Enhancements

*   Payment Gateway Integration
*   Cloud-based Image Uploads
*   SMS Notifications
*   In-app Communication (Chat/Calls)
