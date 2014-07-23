Règles
------
* amener un cetain nombre de noeuds au point d'arrivée
* temps limité pour chaque niveau, à la fin du temps, les noeuds commencent à grossir ce qui rend le jeu plus difficile (et donc leur poids en est affecté)

Gameplay
--------
* des obstacles (flèches, scies...) qui coupent les liens
* des souffleuses, aspirateurs
* différents types de lien
* si le graphe est coupé en plusieurs parties:
    * on garde la plus grande ?
    * on garde toutes les parties de plus de X noeuds
* si les noeuds touchent les murs, ils disparraissent
* des zones sans frottement donc le graphe continue à glisser
* des zones où la raideur des liens est modifié (ou autres propriétés physiques)
* se renseigner sur les moteurs physiques, pour éviter de faire la gestion des collisions
* les ressort font uniquement la force attractive et pas la force répulsive -> physique façon boule de billard

Niveaux
-------
* voir les moteurs physiques, certains ont des outils spéciaux pour dessiner les niveaux et détecter les collisions
* utiliser des images et utiliser la couleur des pixels
* utiliser des fichiers texte en ASCII art (un caractère correspond à une forme)
* aspect graphique : colorier le blob pour avoir l'impression d'une forme (d'où le nom BLOB)

