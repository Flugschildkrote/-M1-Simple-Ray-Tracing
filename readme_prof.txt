Auteur : Alexandre Brunet


Le projet rendu utilise opengl. Il est donc rendu sous forme de projet eclipse.
De plus des resources (images / shaders) sont fornies. Elles sont à mettre dans bin lors du projet eclipse lors de l'execution.
Si ces fichiers ressource ne sont pas présents, le programme ne se lancera pas. 

Ce programme est interactif : 
Déplacement : Flèches directionnelles 
Changer le zoom : Molette souris
Ragarder : Controle + Déplacement souris (il faut peut être cliquer dans la fenêtre pour qu'lle prenne le focus).


Si besion, la résolution de rendu peut être mofifiée avec le paramètre statique 
RESOLUTION_SCALE de la classe Scene (change le ratio de résolution). 
Résolution finale = RESOLUTION_SCALE * (1024*768)
De même il est possible de changer l'ordre du lancer de rayon avec le paramètre statique ORDER de la classe scène.


Les fonctionnalités prises en charge sont : 
-> 3 Formes disponnibles : Plan, Triangle et Sphère
-> éclairage de phong avec prise en charge de plusieurs sources lumineuses
-> Réflections
-> Réfractions 
-> Texturing (uniquement sur les triangles et les plans) 
-> Déplacement de l'utilisateur dans l'environnement avec rendu temps réel.


Remarques : 
Il est possible que le calcul de la réfraction soit incorrect si la caméra se trouve à l'intérieur d'un objet.
De même, il est possible que le calcul de la réfraction soit faussé si plusieurs objets se superposent (sont en collision directe)

Les bibliothèques nécéssaires requises sont : 
gluegen-rt
gluegen-rt-natives-PLATFORM
jogl-all
jogl-all-natives-PLATFORM 

Les scripts de compilation et d'execution pour windows sont fournis.
Il ne faut pas oublier de copier le dossier res dans le dossier bin

