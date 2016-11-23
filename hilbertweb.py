# all the imports
import os
import sqlite3
import hilbert
import liblo
import argparse
from flask import Flask, request, session, g, redirect, url_for, abort, \
     render_template, flash

def parse_args():
    parser = argparse.ArgumentParser(description='Send some OSC messages')
    parser.add_argument('--port', default=57120, type=int,
                    help='oscport')
    parser.add_argument('--host', default="127.0.0.1", 
                    help='oschost')
    parser.add_argument('--dims', default=10, type=int,
                    help='dims')
    parser.add_argument('--bits', default=10, type=int,
                    help='bits')
    args = parser.parse_args()
    return args



app = Flask(__name__)
app.config.from_object(__name__)

@app.route('/location', methods=['GET','POST'])
def location():
    x = min(180.0,max(-180.0,float(request.args.get("x"))))
    y = min(85.0,max(-85.0,float(request.args.get("y"))))
    x = int(screen_max * (x + 180.0) / 360.0)
    y = int(screen_max * (y + 85.0) / (2*85.0))
    h = hilbert.distance_from_coordinates([x,y], 11, 2) # 2048x2048
    for i in range(1,N+1):
        dim_max = 2**(i*p)
        new_h = dim_max * h / total_screen_max
        new_x = hilbert.coordinates_from_distance(new_h, p, i)
        print(new_x)
        new_xf = map(lambda x: x/fp,new_x)
        liblo.send(target, "/mouse"+str(i),*new_xf)
    return ('', 204)

@app.route('/', methods=['GET','POST'])
def root():
    return redirect("/static/hilbert.html",code=302)

if __name__ == "__main__":
    args = parse_args()
    target = liblo.Address(args.host,args.port)
    N = int(args.dims)
    p = int(args.bits)
    fp = 2.0**p
    tbits = N * p
    screen_bits = 11
    screen_max = 2**screen_bits
    total_screen_max = 2**(2*screen_bits)
    dim_max = 2**tbits
    app.run()
