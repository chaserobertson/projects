import sys
import numpy as np

try:
    from OpenGL.GLUT import *
    from OpenGL.GL import *
    from OpenGL.GLU import *
    from OpenGL.GL import glOrtho
    from OpenGL.GLU import gluPerspective
    from OpenGL.GL import glRotated
    from OpenGL.GL import glTranslated
    from OpenGL.GL import glLoadIdentity
    from OpenGL.GL import glMatrixMode
    from OpenGL.GL import GL_MODELVIEW
    from OpenGL.GL import GL_PROJECTION
except:
    print("ERROR: PyOpenGL not installed properly. ")

DISPLAY_WIDTH = 500.0
DISPLAY_HEIGHT = 500.0

#location info is x, y, z, angle, tire angle
camera = [0.0, 0.0, -30.0, 0.0]
#camera = [-3.3057736453110906, -2.0, -15.861526542248907, -67.0]
car = [-25.0, 0.0, 10.0, 0.0, 0.0]


def init(): 
    glClearColor (0.0, 0.0, 0.0, 0.0)
    glShadeModel (GL_FLAT)
    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
    gluPerspective( 90.0, 1.0, 1.0, 100.0)

def drawHouses():
	glPushMatrix()

	# draw front-facing 3 houses
	glPushMatrix()
	drawHouse()

	glTranslated(-20, 0, 0)
	drawHouse()

	glTranslated(40, 0, 0)
	drawHouse()
	glPopMatrix()

	# draw rear-facing 3 houses
	glPushMatrix()
	glRotated(180, 0, 1, 0)
	glTranslated(0, 0, -40)
	drawHouse()

	glTranslated(-20, 0, 0)
	drawHouse()

	glTranslated(40, 0, 0)
	drawHouse()
	glPopMatrix()

	#draw end house
	glRotated(90, 0, 1, 0)
	glTranslated(-20, 0, -30)
	drawHouse()

	glPopMatrix()
    
def drawHouse():
    glLineWidth(2.5)
    glColor3f(1.0, 0.0, 0.0)
    #Floor
    glBegin(GL_LINES)
    glVertex3f(-5.0, 0.0, -5.0)
    glVertex3f(5, 0, -5)
    glVertex3f(5, 0, -5)
    glVertex3f(5, 0, 5)
    glVertex3f(5, 0, 5)
    glVertex3f(-5, 0, 5)
    glVertex3f(-5, 0, 5)
    glVertex3f(-5, 0, -5)
    #Ceiling
    glVertex3f(-5, 5, -5)
    glVertex3f(5, 5, -5)
    glVertex3f(5, 5, -5)
    glVertex3f(5, 5, 5)
    glVertex3f(5, 5, 5)
    glVertex3f(-5, 5, 5)
    glVertex3f(-5, 5, 5)
    glVertex3f(-5, 5, -5)
    #Walls
    glVertex3f(-5, 0, -5)
    glVertex3f(-5, 5, -5)
    glVertex3f(5, 0, -5)
    glVertex3f(5, 5, -5)
    glVertex3f(5, 0, 5)
    glVertex3f(5, 5, 5)
    glVertex3f(-5, 0, 5)
    glVertex3f(-5, 5, 5)
    #Door
    glVertex3f(-1, 0, 5)
    glVertex3f(-1, 3, 5)
    glVertex3f(-1, 3, 5)
    glVertex3f(1, 3, 5)
    glVertex3f(1, 3, 5)
    glVertex3f(1, 0, 5)
    #Roof
    glVertex3f(-5, 5, -5)
    glVertex3f(0, 8, -5)
    glVertex3f(0, 8, -5)
    glVertex3f(5, 5, -5)
    glVertex3f(-5, 5, 5)
    glVertex3f(0, 8, 5)
    glVertex3f(0, 8, 5)
    glVertex3f(5, 5, 5)
    glVertex3f(0, 8, 5)
    glVertex3f(0, 8, -5)
    glEnd()

def drawVehicle():
	glPushMatrix()

	#draw car
	glRotated(car[3], 0, 1, 0)
	glTranslated(car[0], car[1], car[2])
	#glRotated(car[4], 1, 0, 0)
	drawCar()

	#draw tires
	glPushMatrix()
	glTranslated(2, 0, 2)
	glRotated(car[4], 0, 0, 1)
	drawTire()
	glPopMatrix()

	glPushMatrix()
	glTranslated(-2, 0, 2)
	glRotated(car[4], 0, 0, 1)
	drawTire()
	glPopMatrix()

	glPushMatrix()
	glTranslated(-2, 0, -2)
	glRotated(car[4], 0, 0, 1)
	drawTire()
	glPopMatrix()

	glPushMatrix()
	glTranslated(2, 0, -2)
	glRotated(car[4], 0, 0, 1)
	drawTire()
	glPopMatrix()

	glPopMatrix()

def drawCar():
	glLineWidth(2.5)
	glColor3f(0.0, 1.0, 0.0)
	glBegin(GL_LINES)
	#Front Side
	glVertex3f(-3, 2, 2)
	glVertex3f(-2, 3, 2)
	glVertex3f(-2, 3, 2)
	glVertex3f(2, 3, 2)
	glVertex3f(2, 3, 2)
	glVertex3f(3, 2, 2)
	glVertex3f(3, 2, 2)
	glVertex3f(3, 1, 2)
	glVertex3f(3, 1, 2)
	glVertex3f(-3, 1, 2)
	glVertex3f(-3, 1, 2)
	glVertex3f(-3, 2, 2)
	#Back Side
	glVertex3f(-3, 2, -2)
	glVertex3f(-2, 3, -2)
	glVertex3f(-2, 3, -2)
	glVertex3f(2, 3, -2)
	glVertex3f(2, 3, -2)
	glVertex3f(3, 2, -2)
	glVertex3f(3, 2, -2)
	glVertex3f(3, 1, -2)
	glVertex3f(3, 1, -2)
	glVertex3f(-3, 1, -2)
	glVertex3f(-3, 1, -2)
	glVertex3f(-3, 2, -2)
	#Connectors
	glVertex3f(-3, 2, 2)
	glVertex3f(-3, 2, -2)
	glVertex3f(-2, 3, 2)
	glVertex3f(-2, 3, -2)
	glVertex3f(2, 3, 2)
	glVertex3f(2, 3, -2)
	glVertex3f(3, 2, 2)
	glVertex3f(3, 2, -2)
	glVertex3f(3, 1, 2)
	glVertex3f(3, 1, -2)
	glVertex3f(-3, 1, 2)
	glVertex3f(-3, 1, -2)
	glEnd()
	
def drawTire():
	glLineWidth(2.5)
	glColor3f(0.0, 0.0, 1.0)
	glBegin(GL_LINES)
	#Front Side
	glVertex3f(-1, .5, .5)
	glVertex3f(-.5, 1, .5)
	glVertex3f(-.5, 1, .5)
	glVertex3f(.5, 1, .5)
	glVertex3f(.5, 1, .5)
	glVertex3f(1, .5, .5)
	glVertex3f(1, .5, .5)
	glVertex3f(1, -.5, .5)
	glVertex3f(1, -.5, .5)
	glVertex3f(.5, -1, .5)
	glVertex3f(.5, -1, .5)
	glVertex3f(-.5, -1, .5)
	glVertex3f(-.5, -1, .5)
	glVertex3f(-1, -.5, .5)
	glVertex3f(-1, -.5, .5)
	glVertex3f(-1, .5, .5)
	#Back Side
	glVertex3f(-1, .5, -.5)
	glVertex3f(-.5, 1, -.5)
	glVertex3f(-.5, 1, -.5)
	glVertex3f(.5, 1, -.5)
	glVertex3f(.5, 1, -.5)
	glVertex3f(1, .5, -.5)
	glVertex3f(1, .5, -.5)
	glVertex3f(1, -.5, -.5)
	glVertex3f(1, -.5, -.5)
	glVertex3f(.5, -1, -.5)
	glVertex3f(.5, -1, -.5)
	glVertex3f(-.5, -1, -.5)
	glVertex3f(-.5, -1, -.5)
	glVertex3f(-1, -.5, -.5)
	glVertex3f(-1, -.5, -.5)
	glVertex3f(-1, .5, -.5)
	#Connectors
	glVertex3f(-1, .5, .5)
	glVertex3f(-1, .5, -.5)
	glVertex3f(-.5, 1, .5)
	glVertex3f(-.5, 1, -.5)
	glVertex3f(.5, 1, .5)
	glVertex3f(.5, 1, -.5)
	glVertex3f(1, .5, .5)
	glVertex3f(1, .5, -.5)
	glVertex3f(1, -.5, .5)
	glVertex3f(1, -.5, -.5)
	glVertex3f(.5, -1, .5)
	glVertex3f(.5, -1, -.5)
	glVertex3f(-.5, -1, .5)
	glVertex3f(-.5, -1, -.5)
	glVertex3f(-1, -.5, .5)
	glVertex3f(-1, -.5, -.5)
	glEnd()

def display():
    glClear (GL_COLOR_BUFFER_BIT)
    glColor3f (1.0, 1.0, 1.0)
    # viewing transformation
    glMatrixMode(GL_MODELVIEW)
    glLoadIdentity()
    
    glRotated(camera[3], 0, 1, 0)
    glTranslated(camera[0], camera[1], camera[2])

    drawHouses()
    drawVehicle()
    glFlush()

def keyboard(key, x, y):
    if key == chr(27):
        import sys
        sys.exit(0)

    if key == b'a':
        camera[0] += np.cos(np.radians(camera[3]))
        camera[2] += np.sin(np.radians(camera[3]))
    if key == b'd':
        camera[0] -= np.cos(np.radians(camera[3]))
        camera[2] -= np.sin(np.radians(camera[3]))
    if key == b'r':
        camera[1] -= 1
    if key == b'f':
        camera[1] += 1
    if key == b'w':
        camera[2] += np.cos(np.radians(camera[3])) #/5
        camera[0] -= np.sin(np.radians(camera[3])) #/5
    if key == b's':
        camera[2] -= np.cos(np.radians(camera[3])) #/5
        camera[0] += np.sin(np.radians(camera[3])) #/5
    if key == b'q':
        camera[3] -= 1
    if key == b'e':
        camera[3] += 1
    if key == b'h':
        camera[0:4] = [0.0, 0.0, -30.0, 0.0]
        car[0:5] = [-25.0, 0.0, 10.0, 0.0, 0.0]
    if key == b'o':
        glMatrixMode(GL_PROJECTION)
        glLoadIdentity()
        glOrtho(-25, 25, -25, 25, 1, 100)
    if key == b'p':
        glMatrixMode(GL_PROJECTION)
        glLoadIdentity()
        gluPerspective( 90.0, 1.0, 1.0, 100.0)
  
    glutPostRedisplay()

def timer(time):
	car[0] += (0.1 * np.cos(np.radians(car[3])))
	car[2] += (0.1 * np.sin(np.radians(car[3])))
	car[4] -= 3
	#print(camera)
	display()
	glutTimerFunc(int(1000/30), timer, 30)


glutInit(sys.argv)
glutInitDisplayMode (GLUT_SINGLE | GLUT_RGB)
glutInitWindowSize (int(DISPLAY_WIDTH), int(DISPLAY_HEIGHT))
glutInitWindowPosition (100, 100)
glutCreateWindow (b'OpenGL Lab')
init ()
glutDisplayFunc(display)
glutKeyboardFunc(keyboard)
glutTimerFunc(int(1000/30), timer, 30)
glutMainLoop()
