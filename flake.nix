{
  inputs.mvn2nix = {
    url = "github:fzakaria/mvn2nix";

    inputs.nixpkgs.follows = "nixpkgs";
    inputs.utils.follows = "flake-utils";
  };

  outputs = { flake-utils, nixpkgs, mvn2nix, ... }: flake-utils.lib.eachDefaultSystem(system:
  let
    pkgs = import nixpkgs {
      inherit system;
      overlays = [
        (self: super: { jdk11_headless = super.jdk17_headless; })
        mvn2nix.overlay
     ];
    };

    myJre = pkgs.jdk17;

    # For usage, see https://github.com/fzakaria/mvn2nix

    # To generate project-info.json use:
    # nix run .#mvn2nix > mvn2nix-lock.json
    j8080-repo = pkgs.buildMavenRepositoryFromLockFile { file = ./mvn2nix-lock.json; };

    j8080 = pkgs.stdenv.mkDerivation rec {
      pname = "j8080";
      version = "0.1-SNAPSHOT";
      src = ./.;

      jdk = myJre;

      nativeBuildInputs = with pkgs; [ jdk maven makeWrapper ];
      buildPhase = "mvn --offline -Dmaven.repo.local=${j8080-repo} package";

      installPhase = ''
        mkdir -p $out/share/java
        install -Dm644 target/${pname}-${version}.jar $out/share/java

        mkdir -p $out/bin
        makeWrapper ${myJre}/bin/java $out/bin/j8080 --add-flags "-cp $out/share/java/${pname}-${version}.jar li.monoid.j8080.App"
      '';
    };
  in {
    apps = {
      default = {
        type = "app";
        program = "${j8080}/bin/j8080";
      };

      mvn2nix = flake-utils.lib.mkApp { drv = pkgs.mvn2nix; };
    };

    packages = {
      default = j8080;
      j8080 = j8080;
    };
  });
}
