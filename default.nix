with (import <nixpkgs> { });

haskell.lib.buildStackProject {
  name = "dhallbin";
  buildInputs = [ zlib ];
}
