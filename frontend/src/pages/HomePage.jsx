function HomePage() {
    return (
        <>
            <section className="card status-card">
                <div>
                    <h2>AgriSense AI</h2>

                    <p>
                        AgriSense AI is a web application for managing agricultural
                        data, monitoring weather conditions, and generating intelligent
                        irrigation recommendations. The system combines a React frontend,
                        Spring Boot backend, PostgreSQL database, CSV/Excel import and
                        export, Open-Meteo weather data, and a FastAPI machine learning
                        service.
                    </p>
                </div>

                <div className="status-list">
                    <div className="status-item">
                        <span className="status-dot"></span>
                        Spring Boot backend
                    </div>

                    <div className="status-item">
                        <span className="status-dot"></span>
                        PostgreSQL database
                    </div>

                    <div className="status-item">
                        <span className="status-dot"></span>
                        React frontend
                    </div>

                    <div className="status-item">
                        <span className="status-dot"></span>
                        FastAPI ML recommendation service
                    </div>
                </div>
            </section>

            <section className="home-grid">
                <article className="feature-card">
                    <h3>Agricultural Data Management</h3>

                    <p>
                        Create, update, search, and manage crop, parcel, and field
                        activity records associated with your account.
                    </p>
                </article>

                <article className="feature-card">
                    <h3>Weather Monitoring</h3>

                    <p>
                        Retrieve current weather conditions and daily forecasts using
                        geographic coordinates through the Open-Meteo integration.
                    </p>
                </article>

                <article className="feature-card">
                    <h3>AI Irrigation Recommendations</h3>

                    <p>
                        Submit soil, crop, and environmental conditions to the machine
                        learning service and receive irrigation recommendations based
                        on the predicted water requirement.
                    </p>
                </article>

                <article className="feature-card">
                    <h3>Dashboard</h3>

                    <p>
                        View agricultural statistics, search stored records, and manage
                        crops, parcels, and activities from one centralized dashboard.
                    </p>
                </article>

                <article className="feature-card">
                    <h3>Import / Export</h3>

                    <p>
                        Import agricultural records from CSV or Excel files and export
                        user-specific crops, parcels, and activities for portability
                        and backup.
                    </p>
                </article>

                <article className="feature-card">
                    <h3>Secure User Accounts</h3>

                    <p>
                        Access user-specific agricultural data through JWT-based
                        authentication, protected routes, and role-based administrative
                        functionality.
                    </p>
                </article>
            </section>
        </>
    );
}

export default HomePage;