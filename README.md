# lexoffice-demo
In diesem Repository teile ich eines meiner alten Projekte mit Euch. Ich habe dieses Projekt Ende 2018 im Rahmen einer Semesterarbeit erstellt. Die Aufgabenstellung war es, eine Webapp zu entwickeln, die ganz ähnlich wie Doodle Terminabstimmungen ermöglicht. Das heißt, diese App ermöglicht den Usern, Umfragen zu erstellen (und zu bearbeiten), an bestehenden Umfragen teilzunehmen und Umfragen zu Events umzuwandeln. Darüber hinaus werden Teilnehmer über Notifications benachrichtigt, wenn der Initiator einer Umfrage ein Event geplant hat. Die Teilnehmer, welche für den gewählten Zeitpunkt abgestimmt haben, bekommen das Event in der Seidenav angezeigt.

# disclaimer
Ja, dieses Projekt ist 2018 entstanden. Zu dieser Zeit habe ich noch studiert und würde heute wohl einiges anders bauen. Ich habe die vergangenen Tage dazu genutzt, die gröbsten Sachen umzubauen. Dazu zählen:
* Custom Repositories, um Persistence-Belange ausschließlich auf den Persistence-Layer zu begrenzen.
* Unnötige Service-Interfaces entfernt, da diese eine zusätzliche Indirektion darstellten. Ein Austausch der Services ist in diesem Projekt sehr unwahrscheinlich und deshalb -> YAGNI.
* DTO und Entitys aufgesplittet. In der ursprünglichen Version wurden die Entitys auch als DTOs missbraucht (böse, ich weiß). Dementsprechend habe ich neue DTOs erzeugt, die nun zum Informationsaustausch genutzt werden. Die Implementierungsdetails der Entitys sind nun beschützt.

Einige Dinge die ich heute anders machen würde: 
*	Das Domainmodel anders schneiden und fachliche Logik im Domainmodel platzieren. So würden auch kleinere Berechnungen im Frontend wegfallen und das Frontend würde nur noch das Anzeigen, was es vom Backend bekommt. Auf diese Weise lässt sich eine Migration zu einer anderen Frontendtechnologie (wie React zum Beispiel) einfacher realisieren. 
*	Websocket over Polling. Das Frontend ist aktuell sehr gierig und fragt das Backend regelmäßig nach neuen Events und Notifications. Heute würde ich diese Abhängigkeit umdrehen und das Frontend via Websockets aktiv über Veränderungen Informieren. 

Ich habe dieses Projekt dennoch ausgewählt, da es das einzige ist, das ich mit Euch teilen darf und das einen sinnvollen Tech-Stack hat.

# prerequisits
``npm (npm install in src/webapp)``
``mvn``
``java 1.8``
