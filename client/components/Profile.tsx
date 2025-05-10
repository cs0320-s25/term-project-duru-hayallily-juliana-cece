import React from "react";
import { SignOutButton, useUser } from "@clerk/clerk-react";

const profileCardStyle = {
  background: "#f6e6ff",
  borderRadius: "24px",
  padding: "40px 32px",
  boxShadow: "0 4px 24px #e0c3a5",
  maxWidth: "420px",
  margin: "300px auto",
  textAlign: "center",
};

const avatarStyle = {
  fontSize: "4rem",
  marginBottom: "16px",
  filter: "drop-shadow(0 2px 4px #e0c3a5)",
};

const nameStyle = {
  fontWeight: "bold",
  fontSize: "1.5rem",
  color: "#a86fd1",
  marginBottom: "8px",
};

const infoStyle = {
  color: "#b48a54",
  marginBottom: "24px",
};

const Profile: React.FC = () => {
  const { user } = useUser();

  return (
    <div style={profileCardStyle}>
      <div style={avatarStyle} aria-label="User Avatar">🧑‍🍳</div>
      <div style={nameStyle}>
        {user?.firstName || "Your"} {user?.lastName || "Profile"}
      </div>
      <div style={infoStyle}>
        <div>Email: <b>{user?.primaryEmailAddress?.emailAddress || "user@email.com"}</b></div>
        <div>Member since: <b>{user?.createdAt ? new Date(user.createdAt).toLocaleDateString() : "N/A"}</b></div>
      </div>
      <SignOutButton>
        <button
          style={{
            background: "#f9d29d",
            color: "#b48a54",
            border: "none",
            padding: "12px 32px",
            borderRadius: "8px",
            cursor: "pointer",
            fontWeight: "bold",
            fontSize: "1rem",
            boxShadow: "0 2px 8px #e0c3a5",
            transition: "background 0.2s",
          }}
        >
          Log Out
        </button>
      </SignOutButton>
    </div>
  );
};

export default Profile;
