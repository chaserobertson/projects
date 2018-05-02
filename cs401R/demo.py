import numpy as np 

N = 100
K = 784 # 28x28 image


# experiment 1

data = np.random.randn(K, N)

X = np.random.randn(K, 1)

# calc p(x), given KDE
sigma = np.eye( K ) # eyedentity matrix
sigma_inv = np.linalg.pinv(sigma) # still identity matrix

prob = 0.0
#c = 2 * pi *
for i in range( N ):
	mu_i = data[:, i:i+1]

	# eval gaussian #i p(x| mu=mu_i, ss)
	diff = X - mu_i
	# sigma * diff would be elementwise using broadcasting
	prob += c * np.exp(-0.5 * np.dot(diff.T, np.dot(sigma, diff)))

prob /= N


# rewrite above for loop as
diff = X - data # x is kx1, data is kxn, diff is kxn

prob = np.exp( -0.5 * np.sum( diff * np.dot(sigma, diff), axis=0, keepdims=True ) )# elementwise multiply instead of N*N matrix
np.mean( prob )


