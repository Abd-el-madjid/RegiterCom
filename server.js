const express = require('express');
const admin = require('firebase-admin');
const nodemailer = require('nodemailer');
// node server.js
// Initialize Firebase Admin SDK
const serviceAccount = {
 //generated from firebase consol
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
    user: '', // Replace with your Gmail email
    pass: ' // Replace with your  password  check reademe for more info 
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
      from: '', // Replace with your Gmail email
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
