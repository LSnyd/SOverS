let functions = require('firebase-functions');

let admin = require('firebase-admin');

admin.initializeApp();

//Fuehre sendNotification function aus, if data is created,updated what ever in userID oder messageID under messages bullet in database 
exports.sendNotification = functions.database.ref('/messages/{userId}/{messageId}').onWrite((change, context) => {
	
	//Receiver is parent, thaints why no child is mentioned 
	//So when value is changing give out value 
	//get the userId of the person receiving the notification because we need to get their token
	const receiverId = context.params.userId;
	console.log("receiverId: ", receiverId);
	
	//GET USERID AND MESSAGE FROM DATABASE
	//get the user id of the person who sent the message
	//if userid changes, get user_id, save as senderID in Database bullet messages
	const senderId = change.after.child('user_id').val();
	console.log("senderId: ", senderId);
	
	//also get the message if changes
	const message = change.after.child('message').val();
	console.log("message: ", message);
	
	//get the message id. We'll be sending this in the payload
	//message is also parent 
	const messageId = context.params.messageId;
	console.log("messageId: ", messageId);

	//get values from other database branch 
		//Path users und ReceiverID, get sendername
	//query the users node and get the name of the user who sent the message
	return admin.database().ref("/users/" + senderId).once('value').then(snap => {
		const senderName = snap.child("name").val();
		console.log("senderName: ", senderName);
		
		//get values from other database branch 
		//Path users und ReceiverID, get token 
		//get the token of the user receiving the message
		return admin.database().ref("/users/" + receiverId).once('value').then(snap => {
			const token = snap.child("messaging_token").val();
			console.log("token: ", token);
			
			//we have everything we need
			//Build the message payload and send the message
			console.log("Construction the notification message.");
			const payload = {
				data: {
					data_type: "direct_message",
					title: "New Message from " + senderName,
					message: message,
					message_id: messageId,
				}
			};
			
			return admin.messaging().sendToDevice(token, payload)
						.then(function(response) {
							console.log("Successfully sent message:", response);
						  })
						  .catch(function(error) {
							console.log("Error sending message:", error);
						  });
		});
	});
});