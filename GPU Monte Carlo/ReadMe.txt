This is the read me file for JOCL Monte Carlo Framework. (Author: Madhur Bhattad)

The read-me part has the section of implementation, answers, concept and code design as mentioned below. 
Implementation section shows the general way to execute project.
Answers section show the specific cases of evaluation. 
Concept and Code design section shows the flow of the project.

1.) Implementation:
To implement this project, you have to specify the parameters in the main method of EuropeanOptionPrice.java 
For example, the main method contains the following two lines that takes in value of parameters and shows the implementation as mentioned in Answers section.

		EuropeanOptionPrice Option = new EuropeanOptionPrice("Call", 152.35, 0.0001, 0.01, 165, 252);
		System.out.println("The value of Option is: " + Option.value);

Note the parameters that the EuropeanOptionPrice Construction takes are 
String Type    (Should be "Call" or "Put")
double S0      (initial stock price)
double Mu      (the daily return parameter)
double sigma   (the daily risk parameter)
double strike  (the strike price at maturity)
int Maturity   (the time to maturity in days)

2.) Answer to the execution mentioned in part 1, is printed below. After each batch of size 10000, the mean and the standard deviation of the prices is printed.
We also print the stopping criteria. Note the rate of convergence matches the thoretical square root convergence.

Does not stop yet
Mu: 6.307422637130333 Std. Dev: 12.928242195225403
Length of confidence Interval: 45.27668299292021 stop if: 14.142135623730951
Does not stop yet
Mu: 6.397860093798477 Std. Dev: 13.061312800713031
Length of confidence Interval: 45.74271662141801 stop if: 20.0
Does not stop yet
Mu: 6.379622235250598 Std. Dev: 13.083139948627437
Length of confidence Interval: 45.819158634325625 stop if: 24.494897427831784
Does not stop yet
Mu: 6.3738745299762225 Std. Dev: 13.068559192687296
Length of confidence Interval: 45.768094595260614 stop if: 28.284271247461902
Does not stop yet
Mu: 6.367168619367394 Std. Dev: 13.037261693942174
Length of confidence Interval: 45.658485964191065 stop if: 31.622776601683796
Does not stop yet
Mu: 6.370676017303153 Std. Dev: 13.026004062983693
Length of confidence Interval: 45.61906001745666 stop if: 34.64101615137755
Does not stop yet
Mu: 6.363154872428304 Std. Dev: 12.988793883995724
Length of confidence Interval: 45.4887442751688 stop if: 37.416573867739416
Does not stop yet
Mu: 6.360832263027979 Std. Dev: 13.014688444177272
Length of confidence Interval: 45.579430988403026 stop if: 40.0
Does not stop yet
Mu: 6.358098443456302 Std. Dev: 13.01844064575101
Length of confidence Interval: 45.5925717726345 stop if: 42.42640687119285
Does not stop yet
Mu: 6.358371476786664 Std. Dev: 13.04191123778604
Length of confidence Interval: 45.67476937840171 stop if: 44.721359549995796
Stops
The value of Option is: 6.350865951475102

3.) Concepts and Code design:

The purpose of this assignment is to parallelize the calculations using GPU to generate normal random variables and thus evaluate the price of European options. 
Note that since we are restricted to pricing only European options, generating the entire path would be an overkill. 
We only need to simulate the final price of a stock at maturity which we can do easily using analytical solution to Geometric Brownian Motion.
However this doesn't mean we can not parallelize the calculations for other categories of financial derivatives using GPU.

Now coming to code design, we have three classes in this project. 
a.) BoxMullerInGPU.java: This class takes two arrays of uniform random variable and does the Box-Muller transformation to generate two lists of Gaussian random numbers
(This part is done in GPU.)
b.) EuropeanOptionPayOutInGPU.java: This class takes in Type, s0, mu, sigma, strike, Maturity, and a list of Gaussian random numbers and gives a list of simulated payouts
(This part is also done in GPU.)
c.) EuropeanOptionPrice.java: This class takes in Type, s0, mu, sigma, strike, and Maturity
It contains our main method and also is responsible for stopping criteria implementation.
For our purposes we have hardcoded the tolerance(as 0.1), required accuracy(as 0.96 or 96%) and batch size of each simulation as 10000.
Note that we have used smaller batch size only for the purpose of showing the code convergence. We should use a larger batch size for faster convergence.
(It avoids calculation duplications in evaluation of stopping criteria)
For our purposes we have used the length of confidence interval as a measure of stopping criteria.
We stop if the length of confidence interval falls below tol*sqrt(n), where n is the number of simulations required.

Note that the answer we have got in part 2 is also significantly close to our previous valuations. We also did junit to prove our code.
Note in our test cases for test_EuropeanOptionPrice.java, we did 3 tests.
test1() is a deep out of money European call option.
test2() is a deep out of money European put option.
test3() is a test for put-call parity.

  
