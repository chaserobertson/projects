# Import a library of functions called 'pygame'
import pygame
from math import pi
import numpy as np

class Point:
	def __init__(self,x,y):
		self.x = x
		self.y = y
	def getCoord(self):
		return [self.x, self.y]
	def getHomo(self):
		return [self.x, self.y, 1]

class Point3D:
	def __init__(self,x,y,z):
		self.x = x
		self.y = y
		self.z = z
	def getCoord(self):
		return [self.x, self.y, self.z]
	def getHomo(self):
		return [self.x, self.y, self.z, 1]
		
class Line3D():
	def __init__(self, start, end):
		self.start = start
		self.end = end

def loadOBJ(filename):
	vertices = []
	indices = []
	lines = []
	
	f = open(filename, "r")
	for line in f:
		t = str.split(line)
		if not t:
			continue
		if t[0] == "v":
			vertices.append(Point3D(float(t[1]),float(t[2]),float(t[3])))
			
		if t[0] == "f":
			for i in range(1,len(t) - 1):
				index1 = int(str.split(t[i],"/")[0])
				index2 = int(str.split(t[i+1],"/")[0])
				indices.append((index1,index2))
			
	f.close()
	
	#Add faces as lines
	for index_pair in indices:
		index1 = index_pair[0]
		index2 = index_pair[1]
		lines.append(Line3D(vertices[index1 - 1],vertices[index2 - 1]))
		
	#Find duplicates
	duplicates = []
	for i in range(len(lines)):
		for j in range(i+1, len(lines)):
			line1 = lines[i]
			line2 = lines[j]
			
			# Case 1 -> Starts match
			if line1.start.x == line2.start.x and line1.start.y == line2.start.y and line1.start.z == line2.start.z:
				if line1.end.x == line2.end.x and line1.end.y == line2.end.y and line1.end.z == line2.end.z:
					duplicates.append(j)
			# Case 2 -> Start matches end
			if line1.start.x == line2.end.x and line1.start.y == line2.end.y and line1.start.z == line2.end.z:
				if line1.end.x == line2.start.x and line1.end.y == line2.start.y and line1.end.z == line2.start.z:
					duplicates.append(j)
					
	duplicates = list(set(duplicates))
	duplicates.sort()
	duplicates = duplicates[::-1]
	
	#Remove duplicates
	for j in range(len(duplicates)):
		del lines[duplicates[j]]
	
	return lines

def loadHouse():
    house = []
    #Floor
    house.append(Line3D(Point3D(-5, 0, -5), Point3D(5, 0, -5)))
    house.append(Line3D(Point3D(5, 0, -5), Point3D(5, 0, 5)))
    house.append(Line3D(Point3D(5, 0, 5), Point3D(-5, 0, 5)))
    house.append(Line3D(Point3D(-5, 0, 5), Point3D(-5, 0, -5)))
    #Ceiling
    house.append(Line3D(Point3D(-5, 5, -5), Point3D(5, 5, -5)))
    house.append(Line3D(Point3D(5, 5, -5), Point3D(5, 5, 5)))
    house.append(Line3D(Point3D(5, 5, 5), Point3D(-5, 5, 5)))
    house.append(Line3D(Point3D(-5, 5, 5), Point3D(-5, 5, -5)))
    #Walls
    house.append(Line3D(Point3D(-5, 0, -5), Point3D(-5, 5, -5)))
    house.append(Line3D(Point3D(5, 0, -5), Point3D(5, 5, -5)))
    house.append(Line3D(Point3D(5, 0, 5), Point3D(5, 5, 5)))
    house.append(Line3D(Point3D(-5, 0, 5), Point3D(-5, 5, 5)))
    #Door
    house.append(Line3D(Point3D(-1, 0, 5), Point3D(-1, 3, 5)))
    house.append(Line3D(Point3D(-1, 3, 5), Point3D(1, 3, 5)))
    house.append(Line3D(Point3D(1, 3, 5), Point3D(1, 0, 5)))
    #Roof
    house.append(Line3D(Point3D(-5, 5, -5), Point3D(0, 8, -5)))
    house.append(Line3D(Point3D(0, 8, -5), Point3D(5, 5, -5)))
    house.append(Line3D(Point3D(-5, 5, 5), Point3D(0, 8, 5)))
    house.append(Line3D(Point3D(0, 8, 5), Point3D(5, 5, 5)))
    house.append(Line3D(Point3D(0, 8, 5), Point3D(0, 8, -5)))
	
    return house

def loadCar():
    car = []
    #Front Side
    car.append(Line3D(Point3D(-3, 2, 2), Point3D(-2, 3, 2)))
    car.append(Line3D(Point3D(-2, 3, 2), Point3D(2, 3, 2)))
    car.append(Line3D(Point3D(2, 3, 2), Point3D(3, 2, 2)))
    car.append(Line3D(Point3D(3, 2, 2), Point3D(3, 1, 2)))
    car.append(Line3D(Point3D(3, 1, 2), Point3D(-3, 1, 2)))
    car.append(Line3D(Point3D(-3, 1, 2), Point3D(-3, 2, 2)))
    #Back Side
    car.append(Line3D(Point3D(-3, 2, -2), Point3D(-2, 3, -2)))
    car.append(Line3D(Point3D(-2, 3, -2), Point3D(2, 3, -2)))
    car.append(Line3D(Point3D(2, 3, -2), Point3D(3, 2, -2)))
    car.append(Line3D(Point3D(3, 2, -2), Point3D(3, 1, -2)))
    car.append(Line3D(Point3D(3, 1, -2), Point3D(-3, 1, -2)))
    car.append(Line3D(Point3D(-3, 1, -2), Point3D(-3, 2, -2)))
    #Connectors
    car.append(Line3D(Point3D(-3, 2, 2), Point3D(-3, 2, -2)))
    car.append(Line3D(Point3D(-2, 3, 2), Point3D(-2, 3, -2)))
    car.append(Line3D(Point3D(2, 3, 2), Point3D(2, 3, -2)))
    car.append(Line3D(Point3D(3, 2, 2), Point3D(3, 2, -2)))
    car.append(Line3D(Point3D(3, 1, 2), Point3D(3, 1, -2)))
    car.append(Line3D(Point3D(-3, 1, 2), Point3D(-3, 1, -2)))

    return car

def loadTire():
    tire = []
    #Front Side
    tire.append(Line3D(Point3D(-1, .5, .5), Point3D(-.5, 1, .5)))
    tire.append(Line3D(Point3D(-.5, 1, .5), Point3D(.5, 1, .5)))
    tire.append(Line3D(Point3D(.5, 1, .5), Point3D(1, .5, .5)))
    tire.append(Line3D(Point3D(1, .5, .5), Point3D(1, -.5, .5)))
    tire.append(Line3D(Point3D(1, -.5, .5), Point3D(.5, -1, .5)))
    tire.append(Line3D(Point3D(.5, -1, .5), Point3D(-.5, -1, .5)))
    tire.append(Line3D(Point3D(-.5, -1, .5), Point3D(-1, -.5, .5)))
    tire.append(Line3D(Point3D(-1, -.5, .5), Point3D(-1, .5, .5)))
    #Back Side
    tire.append(Line3D(Point3D(-1, .5, -.5), Point3D(-.5, 1, -.5)))
    tire.append(Line3D(Point3D(-.5, 1, -.5), Point3D(.5, 1, -.5)))
    tire.append(Line3D(Point3D(.5, 1, -.5), Point3D(1, .5, -.5)))
    tire.append(Line3D(Point3D(1, .5, -.5), Point3D(1, -.5, -.5)))
    tire.append(Line3D(Point3D(1, -.5, -.5), Point3D(.5, -1, -.5)))
    tire.append(Line3D(Point3D(.5, -1, -.5), Point3D(-.5, -1, -.5)))
    tire.append(Line3D(Point3D(-.5, -1, -.5), Point3D(-1, -.5, -.5)))
    tire.append(Line3D(Point3D(-1, -.5, -.5), Point3D(-1, .5, -.5)))
    #Connectors
    tire.append(Line3D(Point3D(-1, .5, .5), Point3D(-1, .5, -.5)))
    tire.append(Line3D(Point3D(-.5, 1, .5), Point3D(-.5, 1, -.5)))
    tire.append(Line3D(Point3D(.5, 1, .5), Point3D(.5, 1, -.5)))
    tire.append(Line3D(Point3D(1, .5, .5), Point3D(1, .5, -.5)))
    tire.append(Line3D(Point3D(1, -.5, .5), Point3D(1, -.5, -.5)))
    tire.append(Line3D(Point3D(.5, -1, .5), Point3D(.5, -1, -.5)))
    tire.append(Line3D(Point3D(-.5, -1, .5), Point3D(-.5, -1, -.5)))
    tire.append(Line3D(Point3D(-1, -.5, .5), Point3D(-1, -.5, -.5)))
    
    return tire

def translation(x, y, z):
	return np.matrix([[1, 0, 0, x],
						[0, 1, 0, y],
						[0, 0, 1, z],
						[0, 0, 0, 1]])

def rotation(angle):
	return np.matrix([[np.cos(np.radians(angle)), 0, np.sin(np.radians(angle)), 0],
						[0, 1, 0, 0],
						[-np.sin(np.radians(angle)), 0, np.cos(np.radians(angle)), 0],
						[0, 0, 0, 1]])

def rotationZ(angle):
	return np.matrix([[np.cos(np.radians(angle)), -np.sin(np.radians(angle)), 0, 0],
						[np.sin(np.radians(angle)), np.cos(np.radians(angle)), 0, 0],
						[0, 0, 1, 0],
						[0, 0, 0, 1]])

def worldTransform(translation, rotation, object):
	transformation = translation * rotation
		
	for line in object:
		for point in [line.start, line.end]:
			pt = np.matrix(point.getHomo()).T
			pt = transformation * pt
			point.x = pt[0]
			point.y = pt[1]
			point.z = pt[2]
	return object

def draw(world_to_camera, line, color):
	start = np.matrix(line.start.getHomo()).T
	end = np.matrix(line.end.getHomo()).T
	c_start = world_to_camera * start
	c_end = world_to_camera * end
	clip_start = np.dot(clip_matrix, c_start)
	clip_end = np.dot(clip_matrix, c_end)
	if not clip(clip_start, clip_end):
		p_start = (clip_start / clip_start[3])
		p_end = (clip_end / clip_end[3])
		canon_start = np.delete(p_start, (2), axis=0)
		canon_end = np.delete(p_end, (2), axis=0)
		screen_start = canon_to_screen * canon_start
		screen_end = canon_to_screen * canon_end
		pygame.draw.line(screen, color, (screen_start[0], screen_start[1]), (screen_end[0], screen_end[1]))

def clip(point1, point2):
	if (point1[0] > point1[3] and point2[0] > point2[3]) or \
	(point1[0] < -point1[3] and point2[0] < -point2[3]) or \
	(point1[1] > point1[3] and point2[1] > point2[3]) or \
	(point1[1] < -point1[3] and point2[1] < -point2[3]) or \
	(point1[2] > point1[3] and point2[2] > point2[3]) or \
	(point1[2] < -point1[3] or point2[2] < -point2[3]):
		return True
	else:
		return False

	
# Initialize the game engine
pygame.init()
 
# Define the colors we will use in RGB format
BLACK = (  0,   0,   0)
WHITE = (255, 255, 255)
BLUE =  (  0,   0, 255)
GREEN = (  0, 255,   0)
RED =   (255,   0,   0)

# Set the height and width of the screen
size = [512, 512]
screen = pygame.display.set_mode(size)

pygame.display.set_caption("Shape Drawing")
pygame.time.set_timer(pygame.USEREVENT, 1)
 
#Set needed variables
done = False
clock = pygame.time.Clock()
start = Point(0.0,0.0)
end = Point(0.0,0.0)
linelist = loadHouse()

#camera info is x, y, z, angle
camera = [0.0, 0.0, 30.0, 180.0]
car = [25.0, 0.0, 10.0, 0.0, 0.0]
sensitivity = 1
zoom_x = 1
zoom_y = 1
n = 1
f = 100
clip_matrix = np.array([[zoom_x, 0, 0, 0],
						[0, zoom_y, 0, 0],
						[0, 0, (f+n)/(f-n), (-2*n*f)/(f-n)],
						[0, 0, 1, 0]])
canon_to_screen = np.matrix([[size[0] / 2, 0, size[0] / 2],
							[0, -size[1] / 2, size[1] / 2],
							[0, 0, 1]])


#Loop until the user clicks the close button.
while not done:
 
	# This limits the while loop to a max of 100 times per second.
	# Leave this out and we will use all CPU we can.
	#clock.tick(100)

	# Clear the screen and set the screen background
	screen.fill(BLACK)

	#Controller Code#
	#####################################################################

	for event in pygame.event.get():
		if event.type == pygame.QUIT: # If user clicked close
			done=True
		elif event.type == pygame.USEREVENT:
			car[0] -= 0.02
			car[4] += 0.75
		
	pressed = pygame.key.get_pressed()

	if pressed[pygame.K_a]:
		camera[0] -= np.cos(np.radians(camera[3]))*sensitivity
		camera[2] -= np.sin(np.radians(camera[3]))*sensitivity
	if pressed[pygame.K_d]:
		camera[0] += np.cos(np.radians(camera[3]))*sensitivity
		camera[2] += np.sin(np.radians(camera[3]))*sensitivity
	if pressed[pygame.K_w]:
		camera[2] += np.cos(np.radians(camera[3]))*sensitivity
		camera[0] -= np.sin(np.radians(camera[3]))*sensitivity
	if pressed[pygame.K_s]:
		camera[2] -= np.cos(np.radians(camera[3]))*sensitivity
		camera[0] += np.sin(np.radians(camera[3]))*sensitivity
	if pressed[pygame.K_r]:
		camera[1] += sensitivity
	if pressed[pygame.K_f]:
		camera[1] -= sensitivity
	if pressed[pygame.K_q]:
		camera[3] += 3*sensitivity
	if pressed[pygame.K_e]:
		camera[3] -= 3*sensitivity
	if pressed[pygame.K_h]:
		camera = [0.0, 0.0, 30.0, 180.0]
		car = [25.0, 0.0, 10.0, 0.0, 0.0]

	#Viewer Code#
	#####################################################################

	world_to_camera_rotate = np.matrix([[np.cos(np.radians(camera[3])), 0, np.sin(np.radians(camera[3])), 0],
										[0, 1, 0, 0],
										[-np.sin(np.radians(camera[3])), 0, np.cos(np.radians(camera[3])), 0],
										[0, 0, 0, 1]])
	world_to_camera_translate = np.matrix([[1, 0, 0, -camera[0]],
											[0, 1, 0, -camera[1]],
											[0, 0, 1, -camera[2]],
											[0, 0, 0, 1]])
	world_to_camera = world_to_camera_rotate * world_to_camera_translate

	rotate0 = rotation(0)
	rotate180 = rotation(180)
		
	red_lines = loadHouse()
	red_lines += worldTransform(translation(20, 0, 0), rotate0, loadHouse())
	red_lines += worldTransform(translation(-20, 0, 0), rotate0, loadHouse())
	red_lines += worldTransform(translation(0, 0, 40), rotate180, loadHouse())
	red_lines += worldTransform(translation(20, 0, 40), rotate180, loadHouse())
	red_lines += worldTransform(translation(-20, 0, 40), rotate180, loadHouse())
	red_lines += worldTransform(translation(30, 0, 20), rotation(-90), loadHouse())

	for s in red_lines:
		draw(world_to_camera, s, RED)

	car_translation = translation(car[0], car[1], car[2])
	car_rotation = rotation(car[3])

	green_lines = worldTransform(car_translation, car_rotation, loadCar())
	for s in green_lines:
		draw(world_to_camera, s, GREEN)


	blue_lines = worldTransform(car_translation * translation(2, 0, 2), rotationZ(car[4]), loadTire())
	blue_lines += worldTransform(car_translation * translation(-2, 0, 2), rotationZ(car[4]), loadTire())
	blue_lines += worldTransform(car_translation * translation(2, 0, -2), rotationZ(car[4]), loadTire())
	blue_lines += worldTransform(car_translation * translation(-2, 0, -2), rotationZ(car[4]), loadTire())

	for s in blue_lines:
		draw(world_to_camera, s, BLUE)

	# Go ahead and update the screen with what we've drawn.
	# This MUST happen after all the other drawing commands.
	pygame.display.flip()
 
# Be IDLE friendly
pygame.quit()
