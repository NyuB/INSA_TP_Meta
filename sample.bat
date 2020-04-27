::java -jar build/libs/JSP.jar --timeout 1 --solver lpt spt geneticJ --genparams mr=0.5 ps=50 sr=0.33 --instance swv01 swv02 swv03 ta50 ta51 ta52 yn1 yn2
::java -jar build/libs/JSP.jar --timeout 10 --solver spt geneticJ --genparams mr=0.5 sr=0.33 ps=50 --instance ta51
java -jar build/libs/JSP.jar --timeout 1 --solver spt lpt geneticJ --genparams mr=0.5 sr=0.75 ps=50 --instance ta51