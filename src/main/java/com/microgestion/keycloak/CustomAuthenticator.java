package com.microgestion.keycloak;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.events.EventType;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

public class CustomAuthenticator implements Authenticator {

    private static final Logger logger = Logger.getLogger(CustomAuthenticator.class);

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        logger.info(">>> Mi Autenticador Personalizado: authenticate()");
        // Aquí va tu lógica principal.
        // Por ejemplo, podrías comprobar algo y luego decidir si el flujo continúa.
        // Si todo está bien, llamas a success().
        //context.success();

        // Si necesitas que el usuario ingrese datos, crearías un formulario:
        // Este método se llama para presentar el formulario de inicio de sesión.
        // Usaremos una plantilla estándar de Keycloak o una personalizada.
        Response challenge = context.form().createLoginUsernamePassword();
        context.challenge(challenge);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        logger.info(">>> Mi Autenticador Personalizado: action()");
        // Este método se ejecuta cuando el usuario envía el formulario.
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String username = formData.getFirst("username");
        String password = formData.getFirst("password");

        // 1. Validación básica de los datos del formulario.
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            context.failure(AuthenticationFlowError.INVALID_CREDENTIALS, 
                context.form().setError("invalidCredentials").createLoginUsernamePassword());
            return;
        }

        KeycloakSession session = context.getSession();
        RealmModel realm = context.getRealm();

        // 2. Buscar al usuario en la base de datos de Keycloak.
        UserModel user = session.users().getUserByUsername(realm, username);

        if (user!= null) {
            // --- CASO 1: EL USUARIO EXISTE ---
            logger.infof("Usuario '%s' encontrado. Validando contraseña...", username);
            
            // Validar la contraseña proporcionada.
            boolean isPasswordValid = user.credentialManager().isValid(UserCredentialModel.password(password));

            if (isPasswordValid) {
                context.setUser(user);
                context.success(); // ¡Inicio de sesión exitoso!
            } else {
                // Contraseña incorrecta.
                context.failure(AuthenticationFlowError.INVALID_CREDENTIALS, 
                    context.form().setError("invalidCredentials").createLoginUsernamePassword());
            }
        } else {
            // --- CASO 2: EL USUARIO NO EXISTE ---
            logger.infof("Usuario '%s' no encontrado. Creando nuevo usuario...", username);

            // Crear el nuevo usuario.
            UserModel newUser = session.users().addUser(realm, username);
            newUser.setEnabled(true);
            
            // Establecer la contraseña (Keycloak la hasheará automáticamente).
            newUser.credentialManager().updateCredential(UserCredentialModel.password(password));

            // ¡CRUCIAL! Adjuntar el nuevo usuario al contexto del flujo.
            context.setUser(newUser);

            // (Opcional) Emitir un evento de registro para auditoría.
            context.getEvent().event(EventType.REGISTER).user(newUser).success();

            // ¡Creación e inicio de sesión exitosos!
            context.success();
        }        
    }

    @Override
    public boolean requiresUser() {
        // Devuelve 'true' si este paso necesita que el usuario ya esté identificado
        // (por ejemplo, después de que haya introducido su nombre de usuario).
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        // Devuelve 'true' si este autenticador debe ejecutarse para este usuario/realm.
        // Puedes usarlo para activar/desactivar el autenticador condicionalmente.
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // Aquí puedes añadir acciones requeridas para el usuario, como "UPDATE_PASSWORD".
    }

    @Override
    public void close() {
        // Este método se llama para limpiar recursos, si los hubiera.
    }
}