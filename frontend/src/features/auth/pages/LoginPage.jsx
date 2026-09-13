import "../auth.css";
import AuthLayout from "../components/AuthLayout";
import LoginForm from "../components/LoginForm";

function LoginPage() {
  return (
    <AuthLayout
      title="Welcome Back"
      subtitle="Sign in to continue managing your farm records."
      switchText="New here?"
      switchLinkText="Create an account"
      switchTo="/auth/register"
    >
      <LoginForm />
    </AuthLayout>
  );
}

export default LoginPage;