import sys
import random
import json
import inspect
from json import JSONEncoder

class Encoder(JSONEncoder):
    def default(self, o):
        return o.__dict__


delta_time = 300000
battery_loss_chance = 0.003 # probability of losing 5% battery
battery_charge_chance = 0.001 # probability that the user will recharge the device
battery_charge_speed = 5
heart_attack_rate = 0.001 # probavility that the user is doing an heart attack
in_charge = False
heart_attack = 0


class User(object):

    def __init__(self):
        self.id = "M-" + str(random.randrange(1000, 9999))
        self.timestamp = 1551955320 + random.randrange(10000)
        self.battery = random.randrange(0, 101, 5)
        self.longitude = random.uniform(-180, 180)
        self.latitude = random.uniform(-180, 180)
        self.state = ""
        self.msg = ""
        self.heart_rate = 50 + random.randrange(20)
        self.temperature = random.randrange(-50, 400) / 10
        self.msg_type = 0
        self.get_state()

    def get_state(self):
        self.state = "Good"
        self.msg = ""
        self.msg_type = 0
        if heart_attack:
            self.state = "Alert"
            self.msg = "Heart rate"
            self.msg_type = 2
        elif self.battery < 10:
            self.state = "Error"
            self.msg = "Out of battery"
            self.msg_type = 1

    def next_iter(self):
        self.timestamp += delta_time + random.randrange(int(delta_time / 60))
        global in_charge
        global heart_attack
        global heart_attack_rate

        # Battery
        if in_charge:
            self.battery += battery_charge_speed
            if self.battery >= 100:
                in_charge = False
                self.battery = 100
        elif random.random() < battery_loss_chance:
            self.battery -= 5
            self.battery = max(0, self.battery)
        elif random.random() < battery_charge_chance:
            in_charge = True

        # Heart
        if random.random() < heart_attack_rate:
            heart_attack = 3
            self.heart_rate += 40
        elif heart_attack:
            heart_attack -= 1
            if not heart_attack:
                self.heart_rate -= 40
        else:
            self.heart_rate += random.randrange(-3,3)
            self.heart_rate = 50 if self.heart_rate < 50 else self.heart_rate
            self.heart_rate = 130 if self.heart_rate > 130 else self.heart_rate

        # Other
        self.longitude += (random.random() - 0.5) / 10000
        self.latitude += (random.random() - 0.5) / 10000
        self.temperature += random.randrange(-5, 5) / 10

        self.get_state()

    def print(self):
        if data_type == "json":
            print(Encoder().encode(self))
        else:
            attributs = [t[1] for t in self.__dict__.items()]
            attributs = [str(a) for a in attributs]
            print(','.join(attributs))

if len(sys.argv) < 2:
    print("USAGE: python3 gen_data.py <NB_DATA> [json|csv]")

data_size = int(sys.argv[1])
data_type = "json" if len(sys.argv) < 3 else sys.argv[2]

u = User()
for _ in range(data_size):
    u.print()
    u.next_iter()
