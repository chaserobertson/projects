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

#camera info is x, y, z, angle, ortho/perspective
camera = [0.0, 0.0, -15.0, 0.0, False]

def init(): 
    glClearColor (0.0, 0.0, 0.0, 0.0)
    glShadeModel (GL_FLAT)
    
def drawHouse ():
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

def display():
    glClear (GL_COLOR_BUFFER_BIT)
    glColor3f (1.0, 1.0, 1.0)
    # viewing transformation
    glMatrixMode(GL_MODELVIEW)
    glLoadIdentity()
    
    glRotated(camera[3], 0, 1, 0)
    glTranslated(camera[0], camera[1], camera[2])

    if camera[4]:
        glMatrixMode(GL_PROJECTION)
        glLoadIdentity()
        glOrtho(-8, 8, -8, 8, 1, 100)
    else:
        glMatrixMode(GL_PROJECTION)
        glLoadIdentity()
        gluPerspective( 90.0, 1.0, 1.0, 100.0)

    drawHouse()
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
        camera[2] += np.cos(np.radians(camera[3]))
        camera[0] -= np.sin(np.radians(camera[3]))
    if key == b's':
        camera[2] -= np.cos(np.radians(camera[3]))
        camera[0] += np.sin(np.radians(camera[3]))
    if key == b'q':
        camera[3] -= 1
    if key == b'e':
        camera[3] += 1
    if key == b'h':
        camera[0:4] = [0, 0, -15, 0]
    if key == b'o':
        camera[4] = True
    if key == b'p':
        camera[4] = False
  
    glutPostRedisplay()


glutInit(sys.argv)
glutInitDisplayMode (GLUT_SINGLE | GLUT_RGB)
glutInitWindowSize (int(DISPLAY_WIDTH), int(DISPLAY_HEIGHT))
glutInitWindowPosition (100, 100)
glutCreateWindow (b'OpenGL Lab')
init ()
glutDisplayFunc(display)
glutKeyboardFunc(keyboard)
glutMainLoop()
