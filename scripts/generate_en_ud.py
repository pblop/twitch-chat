#!/usr/bin/env python3
# vim: set fileencoding=utf-8 :

import json

ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!\"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~ "
UPSIDE_DOWN_ALPHABET = ['0', '⥝', 'ᘔ', 'Ɛ', '߈', 'ϛ', '9', 'ㄥ', '8', '6', 'ɐ', 'q', 'ɔ', 'p', 'ǝ', 'ɟ', 'ᵷ', 'ɥ', 'ᴉ', 'ɾ', 'ʞ', 'ꞁ', 'ɯ', 'u', 'o', 'd', 'b', 'ɹ', 's', 'ʇ', 'n', 'ʌ', 'ʍ', 'x', 'ʎ', 'z', 'Ɐ', 'ᗺ', 'Ɔ', 'ᗡ', 'Ǝ', 'Ⅎ', '⅁', 'H', 'I', 'Ր', 'ʞ', 'L', 'W', 'N', 'O', 'P', 'Ꝺ', 'ᴚ', 'S', '⟘', '∩', 'Λ', 'M', 'X', '⅄', 'Z', '¡', ',,', '#', '$', '%', '⅋', ',', ')', '(', '*', '+', '\'', '-', '˙', '/', ':', '⸵', '>', '=', '<', '¿', '@', ']', '\\', ']', '^', '_', '`', '}', '|', '{', '~', ' ']

def upsidedown(s: str) -> str:
  ret = [""]*len(s)
  skip_next = False
  for i, c in enumerate(s):
    if c == '%' and not skip_next:
      # ret[-i-2] and ret[-i] instead of ret[-i-1] are meant to place % and the character beside it
      # in reverse-reverse order (%s -> %s, instead of s%)
      ret[-i-2] = c
      skip_next = True
    elif skip_next:
      ret[-i] = c
      skip_next = False
    else:
      # ret[-i-1] places the current character at its corresponding reversed position
      ret[-i-1] = UPSIDE_DOWN_ALPHABET[ALPHABET.index(c)]

  return "".join(ret)

def main() -> None:
  print("Loading en_us.json...")
  with open("./src/main/resources/assets/twitchchat/lang/en_us.json", "r") as en_us_f:
    en_us = json.load(en_us_f)

  print("Generating en_ud.json...")
  en_ud = {}
  for k, v in en_us.items():
    print(f"{v} => {upsidedown(v)}")
    en_ud[k] = upsidedown(v)

  print("Saving en_ud.json...")
  with open("./src/main/resources/assets/twitchchat/lang/en_ud.json", "w") as en_ud_f:
    json.dump(en_ud, en_ud_f, sort_keys=True, indent=2)

  print("Done.")

if __name__ == "__main__":
  main()
