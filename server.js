const express = require('express');
const admin = require('firebase-admin');
const nodemailer = require('nodemailer');
// node server.js
// Initialize Firebase Admin SDK
const serviceAccount = {
  "type": "service_account",
  "project_id": "registrecom-82307",
  "private_key_id": "ce273d3963e0bac7d15b80cd4017a13768c084a9",
  "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQD2lBthrJKSQpse\nJDcnb4tYm1MwvY9psmRP1QuLtVlgK9cW+te2fF/aZhDuLRjyFyopWOrSGy9KGdtJ\nnPbT81blHl07L7o9vWjBd23kbI/CN8tyT8MUS+BQRFEAeOd4bogh8BEq/N5u6rFn\n/A2lIztn77kJf0HgfzqWlWsZry9JsKFxL0KlBTXpEhcoJe5vK3laBdPIbRb5Hbj6\nib7VSPGLFLw+4XNIwLtQCthpMPIPhQOi34wnCiS+NUvZJf6sMJjy/GqdcGl0DvsI\nRp47bxHryxIVxxKLsovGr47lbxTdjhcx2NFYO+eEBCiyt5TrI3VNLLb9KD694yGB\nVqdNv6MhAgMBAAECggEAHHDRljLknopgcCny8yFgaBZVUnlA1AsLmzIqcGE413kD\nCH9jF2Ux3KPaZtBskMrXdfivsA+K5TAFjTB1v1VxU8DMdd3PzFx/G1aKRjc33ynW\nlV8qE/qa+0Mf+S8Y2rkcClcRcd0EDnTTRNhLnh0p1leOBxCGsY1XPYRrO6WLTaNA\nC+fQfFNWoPGhp+Ds8RXK5nMBvgZoYWUNLPzLvpv11qPikBf/J+Ws2ZHtw6OFWjL9\nmhDC31Iye/4+QEndEwkkk2jgjJfSSuCuUx/LWnWx9OVOzQi5BhFuiXBzVtHWohyd\n2o4ALus9RqjYLsB0M3HdKiPtn1Cez3qtuLQr5wf3GQKBgQD9LC7tjL3v3QsZuLFS\njaZYIWnw1O75R5ax7NfAyeKS/RdyMJ/sV6n9uOT4V61ljHD8TiPkCUGBPj1p3VBB\nMBW1DW8vJMygv0v+PEOZvB+Wk6lW35bPWnrYf2E4gU/OUs0npoX372Wt2TYSpNxV\nTfa5Go+eZHAlLr5uyPvvQZ00mQKBgQD5VRJEsLiXjKwrdIY0Tw2F1HK3KkQx7UUf\nCbvfnECFcDNbxufwG90P4sVCmsGN4nbCkVQtCft2M4/0oEcPx3nIwViN+ZTzP4nV\n3IfB647zP86ILRt4hrGLLTq2AOvueFJn09YfxhlSE1isMFpo55tHmZXcZ1EcvCgF\n3siaantvyQKBgQDGafQ1VBIfhFsIw4ilLEJr23QNvLO1RffCZd4JR698h0Hyykzr\niyC8qkU4pG5OoayC8DKMqvxdcAvthfc5v5ejl23Fws9kQ8go7c4FFDylZABZB05I\nH58bQWlOQXnsN1R1eLqRv1suzobkQbQ0Xq+4Dir0db62RVYVuQpJTmEIMQKBgQDs\nTFrO7OlU/8YgPQP/yTgYsNqziwQsiSEsJE6AwxZHIKob/evNHKsqphGraP2tWYaS\nKaesM7y1LnLtNYWDh9p8j/k8POCUlJAuNh4XhAbrtHophw508LqNG6V73m27iR8Y\nNaTGYjmXFqzAK+dTasU4W2+y/dvartwDjDt0VRx9OQKBgQCjMTi7KvRkHskk5f3i\nNlvs+1OEjkDhxUSuPYaW97AYAvesyQF9nCoMNn+BD+K74kZE7jgvb43IPfKrBRFT\nj+Wi5znbhjllSGThDUHNzPZgHaRDsOP8aKNPfm+SgjqereY85lMiizy6bsHBwbjZ\nZvdCtIe1VFKruU07o6mASH+9Yg==\n-----END PRIVATE KEY-----\n",
  "client_email": "firebase-adminsdk-7rikq@registrecom-82307.iam.gserviceaccount.com",
  "client_id": "103469416197880986095",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-7rikq%40registrecom-82307.iam.gserviceaccount.com"
};

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://registrecom-82307-default-rtdb.firebaseio.com" // Update with your Firebase database URL
});

const app = express();
const port = 3000;

// Reference to the Notification table
const notificationRef = admin.database().ref('/Notification');
// Setup nodemailer transporter
const transporter = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: 'registre.coma@gmail.com', // Replace with your Gmail email
    pass: 'ejpdgyajyefutiru' // Replace with your Gmail password
  }
});

// Listen for changes in the Notification table
notificationRef.on('child_added', async (snapshot) => {
  try {
    const notification = snapshot.val();

    if (!notification || !notification.idPersonne) {
      console.error('Invalid notification data');
      return;
    }

    // Fetch the user's FCM token from the Personne table based on the idPersonne
    const snapshotPersonne = await admin.database().ref(`/Personne/${notification.idPersonne}`).once('value');
    const user = snapshotPersonne.val();

    if (!user || !user.token || !user.email) {
      console.error('User not found or FCM token or email not available');
      return;
    }

    const message = {
      notification: {
        title: notification.title || 'Default Title',
        body: notification.contenu || 'Default Body'
      },
      token: user.token
    };

    // Send the notification
    const response = await admin.messaging().send(message);

    console.log('Successfully sent message:', response);

    // Send email
    const mailOptions = {
      from: 'registre.coma@gmail.com', // Replace with your Gmail email
      to: user.email,
      subject: notification.title || 'Default Email Title',
      text: notification.contenu || 'Default Email Body'
    };

    // Send the email
    const emailResponse = await transporter.sendMail(mailOptions);

    console.log('Successfully sent email:', emailResponse);
  } catch (error) {
    console.error('Error:', error);
  }
});

app.listen(port, () => {
  console.log(`Server is running at http://localhost:${port}`);
});
