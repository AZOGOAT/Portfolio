#-------------------------------------------------------------------------------
# Name:        NSI-Project1: pendu
# Purpose: Dans le cadre de notre projet, nous avons réalisé un jeu de pendu
#
# Author:      AZGAOUI Omar Ziyad 1°1 (90% --> tout le programme + explication + leader) - DELMI Amine 1°15 (5% --> turtle + base powerpoint) - GUELZIM Ilias 1°20 (5% --> logigrame)
#
# Created:     28/12/2021
#-------------------------------------------------------------------------------


#*******************************************************************************************************************************************************************************


#----------------------------------------------------------------------------------------
#_____________________________________Bibliothèques______________________________________
#-----------------------------------------------------------------------------------------
from random import choice
from turtle import*
import turtle
import time
#-----------------------------------------------------------------------------------------
#_____________________________________Fonctions___________________________________________
#-----------------------------------------------------------------------------------------
def tiret(mot_a_deviner, historique_lettre_propose):
    hideturtle()
    mot_affiche = []
    for i in mot_a_deviner: # i prend tour à tour toute les lettres du mot à deviner
        if i.upper() in historique_lettre_propose: #le programme affiche les lettres trouvé
            mot_affiche.append(i.upper()+" ")
        else:
            mot_affiche.append("_ ")
    mot_affiche[0] = mot_a_deviner[0].upper()+" " #affiche la première lettre du mot à deviner
    mot_affiche[len(mot_affiche)-1] = mot_a_deviner[len(mot_a_deviner)-1].upper() #affiche la dernière lettre du mot à deviner
    speed(100)
    if len(mot_a_deviner) == 5:
        goto (-70,-35)
        write ("_ " *len(mot_a_deviner), True, font=("Courier", 14, "normal")) # dessine les tirets du bas
        goto (-70,-35)
        write("".join(mot_affiche), True, font=("Courier", 14, "normal")) # dessine les lettres du mot
    elif len(mot_a_deviner) == 10:
        goto (-135,-35)
        write ("_ " *len(mot_a_deviner), True, font=("Courier", 14, "normal")) 
        goto (-135,-35)
        write("".join(mot_affiche), True, font=("Courier", 14, "normal"))
    elif len(mot_a_deviner) == 15:
        goto (-170,-35)
        write ("_ " *len(mot_a_deviner), True, font=("Courier", 14, "normal")) 
        goto (-170,-35)
        write("".join(mot_affiche), True, font=("Courier", 14, "normal")) 
    speed(0)
    return "".join(mot_affiche)

def saisie_lettre(tentatives_utilise):
    t.hideturtle()
    lettre_propose = textinput("Jeu du pendu","Saisir lettre:") #similaire au input() dans python
    lettre_propose.lower() #au cas où l'utilisateur saisirait une interdiction en majuscule
    #_________________________________________________Vérification erreur__________________________________________________
    while str(lettre_propose) in interdiction:
        t.up()
        t.goto(-320, -120)
        t.write("Le contenu que vous avez proposé n'est pas prit en charge.\nAttention aux accents, aux chiffres et aux symboles.", True, font=("Courier", 14, "normal"))
        lettre_propose = textinput("Jeu du pendu","Saisir lettre:") 
        tentatives_utilise -= 1
        t.reset()
        t.hideturtle()

    while len(lettre_propose)>1: #Si il y a plus de deux caractères
        t.up()
        t.goto (-200, -120)
        t.write("Vous avez saisie plus d'une lettre.", True, font=("Courier", 14, "normal"))
        lettre_propose = textinput("Jeu du pendu","Veuillez saisir une seule lettre: ")
        tentatives_utilise -= 1
        t.reset()
        t.hideturtle()

    while lettre_propose.upper() in historique_lettre_propose: #si la lettre a déjà été proposée
        t.up()
        t.goto (-200, -120)
        t.write("Cette lettre a déjà été saisie.", True, font=("Courier", 14, "normal"))
        lettre_propose = textinput("Jeu du pendu",'Veuillez saisir une nouvelle lettre: ')
        tentatives_utilise -= 1
        t.reset()
        t.hideturtle()
    return lettre_propose.upper()
    #_______________________________________________________________________________________________________________________

def dessin_bonhomme (erreurs_turtle, unite):
    hideturtle()
    if erreurs_turtle == 1: #dessine corde de pendaison
        goto(165, 180)
        down()
        forward(1*unite)
        up()
        right(90)
    elif erreurs_turtle == 2: #dessine tete
        goto(165, 150)
        down()
        circle(unite/2)
        up()
    elif erreurs_turtle == 3: # dessine torse
        goto(165,150)
        left(90)
        forward(unite)
        down()
        forward(unite)
        up()
    elif erreurs_turtle == 4: # dessine premier bras
        goto(165, 90)
        backward(unite)
        forward(unite/2)
        left(45)
        down()
        forward(unite)
        up()
    elif erreurs_turtle == 5: # dessine deuxieme bras
        goto(186, 84)
        backward(unite)
        right(90)
        down()
        forward(unite)
        up()
        backward(unite)
        left(45)
        down()
        forward(unite)
        up()
    elif erreurs_turtle == 6: # dessine premiere jambe
        goto(165, 75)
        left(45)
        down()
        forward(unite)
        bgcolor("orange")
        up()
    elif erreurs_turtle == 7: # dessine deuxieme jambe + informe le joueur qu'il a perdu
        goto(186, 54)
        backward(unite)
        right(90)
        down()
        forward(unite)
        up()
        t.up()
        t.goto(-300, -130)
        bgcolor("red")
        t.write("Vous avez commis plus de 7 erreurs! Perdu!\nLe mot à deviner était:" + mot_a_deviner.upper(), True, font=("Courier", 14, "normal"))
        t.up()

def dessin_potence(unite):
    hideturtle()
    goto(0,0) # on pouvait utiliser à la place la fonction: home()
    down()
    forward(5*unite)
    up()
    backward(2.5*unite)
    left(90)
    down()
    forward(6*unite)
    up()
    backward(1.5*unite)
    right(45)
    down()
    forward(2.1*unite)
    up()
    backward(2.1*unite)
    left(45)
    forward(1.5*unite)
    right(90)
    down()
    forward(3*unite)
    up()
    backward(1*unite)
    right(90)

    
#-----------------------------------------------------------------------------------------
#_____________________________________Variables de base___________________________________
#-----------------------------------------------------------------------------------------
liste_mots_facile = ["balai","cable","ecole","objet","musee","image","frere","droit","japon","texte","scene","homme","grand","album","ligne","paris","temps","armee","livre","cadre"]
liste_mots_moyen = ["decoration", "brouillard","criminelle","entraineur","instrument","adolescent","artificiel","caricature","guitariste","microscope","navigateur","ordinateur","philosophe","presidente","revolution","spectateur","vegetation","trampoline","simulateur","restaurant"]
liste_mots_difficile = ["geocalisation", "circonscription", "experimentation","quotidiennement","affaiblissement","hospitalisation","mathematicienne","confidentialite","instrumentation","sensibilisation","concessionaire","horizentalement","impressioniste","remarquablement","progressivement","personellement","agroalimentaire","acrobatiquement","perpendiculaire","electrification"]
interdiction="1234567890.+-*/²&'(§!çâäàµùûüôöéèêë)-$^=:;,³|@#{[^{}][`´°_*¨£%+/.?~"
unite = 30
ennuye = True 
fin = True
t = turtle.Turtle() #crée une nouvelle tortue
t.hideturtle()
score = 0

#---------------------------------------------------------------------------------------------------------------------------------
#______________________________Début de la réalisation du programme avec appel de fonction________________________________________
#---------------------------------------------------------------------------------------------------------------------------------
title ("Jeu de pendu") # donne un titre à la grande fenêtre
hideturtle()
up()
goto(-329,0)
write("Notice explicative: vous pouvez saisir n'importe quelle lettre de\nl'alphabet tant qu'elle ne comporte pas d'accents. Le but du jeu\nest de réussir à trouver le mot complet sans dépasser un nombre\nd'erreurs limités, en l'occurence 7, au bout du quel le bonhomme\nserait malheuresement pendu. A vous de jouer !", True, font=("Courier", 12, "normal"))
reponse = textinput("Jeu du pendu","Avez vous lu la notice (oui / non):")
while reponse != "oui" and reponse != "non":
    hideturtle()
    up()
    goto(-200,-100)
    write('Veuillez répondre par "oui" ou "non"', True, font=("Courier", 13, "normal"))
    reponse = textinput("Jeu du pendu","Avez vous lu la notice (oui / non):")
    reset()
if reponse == "oui":
    reset()
    hideturtle()
    up()
    goto(-125,0)
    write("Commençons !", True, font=("Courier", 30, "normal"))
    time.sleep(1)
    reset()
    hideturtle()
elif reponse == "non":
    reset()
    hideturtle()
    up()
    goto(-329,-15)
    write("Notice explicative: vous pouvez saisir n'importe quelle lettre de\nl'alphabet tant qu'elle ne comporte pas d'accents. Le but du jeu\nest de réussir à trouver le mot complet sans dépasser un nombre\nd'erreurs limités, en l'occurence 7, au bout du quel le bonhomme\nserait malheuresement pendu. Vous gagné 1 point lorsque quand vous\ncomplétez le niveau facile, 2 points pour le niveau moyen et 3\npoints pour le niveau difficile. Aucun point n'est perdu lorsque\nle niveau n'est pas complété. À vous de jouer !", True, font=("Courier", 12, "normal"))
    goto(-180,-100)
    write("Vous disposez de 10s de plus!", True, font=("Courier", 13, "normal"))
    time.sleep(10)
    reset()
    hideturtle()
    up()
    goto(-135,0)
    write("Commençons !", True, font=("Courier", 30, "normal"))
    time.sleep(1)
    reset()
    hideturtle()
 #__________________________________________________________________________________________________

while ennuye: #permet de crée une boucle "infinie" jusqu'à ce que l'utilisateur ne souhaite plus joué
    bgcolor("white") #couleur de fond
    hideturtle()
    t.hideturtle()
  #___________________________________________Création du niveau de difficulté______________________
    up()
    goto(-235,70)
    write("Veuillez choisir un niveau de difficulté:", True, font=("Courier", 14, "normal"))
    up()
    goto(-155,40)
    speed(100)
    write('- Saisir "f" pour facile', True, font=("Courier", 14, "normal"))
    up()
    goto(-155,10)
    write('- Saisir "m" pour moyen', True, font=("Courier", 14, "normal"))
    up()
    goto(-155,-20)
    write('- Saisir "d" pour difficile', True, font=("Courier", 14, "normal"))
    reponse2 = textinput("Jeu du pendu",'Veuillez saisir: "f", "m" ou "d"')
    while reponse2 != "f" and reponse2 != "m" and reponse2 != "d":
        hideturtle()
        up()
        goto(-225,-100)
        write('Veuillez répondre par "f" ou "m" ou "d"', True, font=("Courier", 14, "normal"))
        reponse2 = textinput("Jeu du pendu",'Veuillez saisir: "f", "m" ou "d"')
        reset()
    if reponse2 == "f":
        reset()
        hideturtle()
        mot_a_deviner = choice(liste_mots_facile)#la variable mot à deviner est égal à ce que retourne la fonction choice(...)
    elif reponse2 == "m":
        reset()
        hideturtle()
        mot_a_deviner = choice(liste_mots_moyen)
    elif reponse2 == "d":
        reset()
        hideturtle()
        mot_a_deviner = choice(liste_mots_difficile)
 #__________________________________________________________________________________________________________________________   
    #on a définie ces variable à l'intérieur de la boucle ennuie pour que le programme fonctionne
    #correctement si jamais l'utilisateur souhaitait rejouer. En effet, ces varibles se voit souvent
    #modifiés dans le programme
    historique_lettre_propose = ""
    nb_erreurs = 0
    erreurs_turtle = 0
    tentatives_utilise = 0
    hideturtle()
    t.hideturtle()
    potence = dessin_potence(unite) #la variable potence est égal à ce que retourne la fonction dessin_potence(...), en l'ocurrence un dessin
  
  #________________________________________Début du jeu__________________________________________________  
    while fin :
        hideturtle()
        t.hideturtle()
        t.up()
        up()
        mot_affiche = tiret(mot_a_deviner, historique_lettre_propose) #la variable mot_affiché est égal à ce que retourne la fonction tiret(...)
        t.up()
        t.goto(-80,250)
        t.write("Nombre d'erreurs à ne pas dépasser: " + str(7-nb_erreurs), True, font=("Courier", 14, "normal")) #compte à rebour en fonction du nombre de tenatives restantes
        t.up()
        t.goto(120,220)
        t.write("Votre score: " + str(score) + " pts", True,font=("Courier", 14, "normal"))
        if "_" not in mot_affiche: # permet de casser la boucle si l'utilisateur a trouvé toute les lettres
            bgcolor("green")
            t.up()
            t.goto(-335,-120)
            t.write("Félicitation! Vous avez gagné! Nombre de tentatives total: " + str(tentatives_utilise), True, font=("Courier", 14, "normal") )
            t.up()
            # on attribue ci-dessous le score en fonction du niveau de difficulté
            if reponse2 == "f":
                score += 1
            elif reponse2 == "m":
                score += 2
            elif reponse2 == "d":
                score += 3
            t.goto(-300,-150)
            t.write("Votre score est égal à:" + str(score), True,font=("Courier", 14, "normal"))
            fin = False
            break #permet de sortir directement de la boucle sans réaliser les commandes qui suivent
        if nb_erreurs == 7: #permet de casser la boucle si l'utilisateur atteints les 7 erreurs
            fin = False #met fin à la boucle secondaire
            break #permet de sortir directement de la boucle sans réaliser les commandes qui suivent
        lettre_propose = saisie_lettre(tentatives_utilise) #l'utilisateur saisie une lettre avec toutes les verifications que cela inclut
        historique_lettre_propose += lettre_propose.upper() + ","
        goto(-335,-75)
        write("Lettres utilisées: " + historique_lettre_propose, True, font=("Courier", 14, "normal"))
        if lettre_propose.lower() in mot_a_deviner:
            t.up()
            t.goto(-250,-120)
            t.write("Bien joué! Vous avez trouvé une lettre 👍", True, font=("Courier", 14, "normal"))
            time.sleep(0.5)
        if lettre_propose.lower() not in mot_a_deviner:
            nb_erreurs += 1
            erreurs_turtle = nb_erreurs
            t.up()
            t.goto(-250,-120)
            t.write("Dommage, ce n'est pas la bonne lettre 😞.", True, font=("Courier", 14, "normal"))
            time.sleep(0.5)
        t.reset()
        t.hideturtle()
        pendu = dessin_bonhomme (erreurs_turtle, unite)
        erreurs_turtle = 0 # on réinitialise la valeur pour pas que le programme redessine une partie du corps maladroitement
        tentatives_utilise +=1
  #_________________________________________________________________________________________________________________________  
    
  #__________________________________Demande si l'utilisateur souhaite rejouer____________________________  
    reponse3 = textinput("Jeu du pendu", "Voulez vous re-jouer (oui / non) ?")
    while reponse3 != "oui" and reponse3 != "non":
        t.reset()
        up()
        goto(-200,0)
        write('Veuillez répondre par "oui" ou "non"', True, font=("Courier", 13, "normal"))
        reponse3 = textinput("Jeu du pendu","Avez vous lu la notice (oui / non):")
        reset()
        t.reset()
    if reponse3 == "oui":
        reset()
        t.reset()
        fin = True
    elif reponse3 == "non":
        reset()
        t.reset()
        bgcolor("white")
        t.up()
        hideturtle()
        t.hideturtle()
        up()
        goto(0,0)
        write("Merci d'avoir joué !", True, align="center", font=("Courier", 25, "normal"))
        up()
        goto(-260,-50)
        write("Voici votre score finale: " + str(score) + " pts", True, font=("Courier", 20, "normal"))
        ennuye = False #met fin à la foucle initial et donc au jeu
mainloop()
#__________________________________________________________________________________________________________
#*******************************************************************************************************************************************************************************