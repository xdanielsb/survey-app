## Deploy to Production

If the user profile is not automatically loaded during deployment, you need to manually import it into each Keycloak realm:

1. Open **Realm Settings** in the Keycloak Admin Console.
2. Navigate to **User Profile**.
3. Click **JSON Editor**.
4. Copy and paste the contents of `infra/keycloak/user-profile.json` into the editor.
5. Save the changes.
