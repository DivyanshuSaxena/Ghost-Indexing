#!/bin/python3
import matplotlib.pyplot as plt
import pandas as pd
import sys

data1 = pd.read_csv(sys.argv[1], header=None, delimiter=r"\s+", index_col=0)
data2 = pd.read_csv(sys.argv[2], header=None, delimiter=r"\s+", index_col=0)

data = pd.concat([data1, data2], axis=1)
data.columns = ["Elastic Search", "Ghost Index"]

ax = data.plot(lw=4,colormap='jet',marker='.',markersize=1,title='Query Runtime Comparisions')
#ax = data.plot(lw=2,marker='.',markersize=1,title='Query Runtime Comparisions')
ax.set_xlabel("Result Count")
ax.set_ylabel("Time Taken (in ms)")

a = data1.plot()
data2.plot(ax = a)

plt.show()
