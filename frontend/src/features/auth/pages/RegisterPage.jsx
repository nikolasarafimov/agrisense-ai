import "../auth.css";
import AuthLayout from "../components/AuthLayout";
import RegisterForm from "../components/RegisterForm";

function RegisterPage() {
  return (
    <AuthLayout
      title="Create Account"
      subtitle="Set up your profile and start tracking your agricultural data."
      switchText="Already have an account?"
      switchLinkText="Log in"
      switchTo="/auth/login"
    >
      <RegisterForm />
    </AuthLayout>
  );
}

export default RegisterPage;