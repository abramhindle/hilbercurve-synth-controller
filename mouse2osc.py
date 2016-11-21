from Xlib import display
import liblo
import hilbert
import argparse
import time


disp_root = display.Display().screen().root

def getXY():
    data = disp_root.query_pointer()._data
    return data["root_x"], data["root_y"]

def parse_args():
    parser = argparse.ArgumentParser(description='Send some OSC messages')
    parser.add_argument('--port', default=57120, type=int,
                    help='oscport')
    parser.add_argument('--host', default="127.0.0.1", 
                    help='oschost')
    parser.add_argument('--dims', default=2, type=int,
                    help='dims')
    parser.add_argument('--bits', default=10, type=int,
                    help='bits')
    args = parser.parse_args()
    return args

def main(args):
    screen = display.Display().screen()
    swidth  = screen["width_in_pixels"]
    sheight = screen["height_in_pixels"]
    target = liblo.Address(args.host,args.port)
    N = int(args.dims)
    p = int(args.bits)
    tbits = N * p
    fp = 2.0**p
    screen_max = 2**11
    total_screen_max = 2**(2*11)
    dim_max = 2**tbits
    last_h = -1
    while True:
        x,y = getXY()
        x = screen_max  * x / swidth
        y = screen_max  * y / sheight
        h = hilbert.distance_from_coordinates([x,y], 11, 2) # 2048x2048
        new_h = dim_max * h / total_screen_max
        new_x = hilbert.coordinates_from_distance(new_h, p, N)
        if last_h != new_h:
            print (h,new_h,new_x)
            new_xf = map(lambda x: x/fp,new_x)
            liblo.send(target, "/mouse",*new_xf)
        last_h = new_h
        time.sleep(1.0/30.0);

if __name__ == "__main__":
    args = parse_args()
    main(args)
