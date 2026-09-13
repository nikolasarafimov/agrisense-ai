import { Link } from "react-router-dom";

function AuthLayout({
                        title,
                        subtitle,
                        switchText,
                        switchLinkText,
                        switchTo,
                        children,
                    }) {
    return (
        <div className="auth-shell">
            <div className="auth-visual">
                <h1>AgriSense AI</h1>
                <p>
                    Manage crops, parcels, and activities, monitor weather conditions, and
                    receive intelligent irrigation recommendations from one platform.
                </p>
            </div>

            <section className="auth-card" aria-label={title}>
                <header className="auth-header">
                    <h2>{title}</h2>
                    <p>{subtitle}</p>
                </header>

                {children}

                <p className="auth-switch">
                    {switchText} <Link to={switchTo}>{switchLinkText}</Link>
                </p>
            </section>
        </div>
    );
}

export default AuthLayout;