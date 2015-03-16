IntelligentEmailClassifier
==========================


Even after the advent of newer technologies such as instant messaging and VoIP, email remains the top most application of the Internet, intranets and extranets. Though the email system is popular, powerful, cost-effective and efficient, it has some shortcomings like it remains an unmanaged medium and it is a vast store of unstructured information. Hence email database will effectively manage by knowledgeable workers only if: a) It enables classification within stringent legitimate framework b) It can be searched/sorted and mined in ways that make it useful. In our project, emails (text file) which are copied to the main folder will be classified and move to the respective folders of the users and this event is logged in database. Upon receiving the file in user folder, one watcher thread will again classify the email depending on its content like it is spam or contains some place information to the folder specified in the user settings. The email will be in XML format to reduce the complexities. 
