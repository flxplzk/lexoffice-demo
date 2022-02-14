# lexoffice demo 

In diesem Projekt teile ich eines meiner alten Projekte mit Euch. Ich habe dieses Projekt Ende 2018, im Rahmen einer Semesterarbeit, erstellt. 
Die Aufgabenstellung war, eine Webapp zu entwickeln, die ganz ähnlich wie Doodle Terminabstimmungen ermöglicht. Das heißt, diese App ermöglicht den Usern Umfragen zu erstellen (und zu bearbeiten), an bestehenden Umfragen teilzunehmen und Umfragen zu Events umzuwandeln. Darüber hinaus werden Teilnehmer, über Notifications, benachrichtigt, wenn der Initiator einer Umfrage ein Event geplant hat. Die Teilnehmer, welche für den gewählten Zeitpunkt abgestimmt haben, bekommen das Event in der Seidenav angezeigt und werden gleichzeitig Benachrichtigt. 
# disclaimer 

Ja, dieses Projekt ist 2018 entstanden. Zu dieser Zeit habe ich noch studiert und würde heute wohl einiges anders bauen. Ich habe die vergangenen Tage dazu nutzt die gröbsten Sachen umzubauen. Dazu zählen: 
- Custom Repositories, um Persistence-Belange ausschließlich auf den Persistence-Layer zu begrenzen.
- Unnötige Service-Interfaces entfernt, da diese eine unnötige Indirekten darstellten. Ein Austausch der Services ist in diesem Projekt sehr unwahrscheinlich und deshalb -> YAGNI.
- DTO und Entitys auf gesplittet. In der ursprünglichen Version wurden die Entitys auch als DTOs missbraucht (böse ich weiß). Dementsprechend habe ich neue DTOs erzeugt, die nun zum Informationsaustausch genutzt werden. Die Implementierungsdetails der Entitys sind nun beschützt. 

Das Domainmodel würde ich heute wohl auch anders designen. Das lässt sich jetzt aber nicht mehr sinnvoll umsetzen, ohne große Teile der Applikation neu zu schreiben. Ich habe dieses Projekt dennoch ausgewählt, da es das einzige ist, dass ich mit Euch Teilen darf und einen sinnvollen Tech-Stack hat. 

# prerequisits

npm (npm install in src/webapp) 
mvn 
java 1.8
