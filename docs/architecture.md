# Architecture

## Registers

| Symbol | Size | Name                    | Addr |
|--------|------|-------------------------|------|
| PC     | 16   | Program Counter         |      |
| SP     | 16   | Stack pointer           |      |
| B,C    | 8,8  | General purpose         | 0,1  |
| D,E    | 8,8  | General purpose         | 2,3  |
| H,L    | 8,8  | General purpose         | 4,5  |
| W,Z    | 8,8  | Temporary               |      |
| M      |      | Pseudo address register | 6    |

## Lines

| Symbol | Size | Name          |
|--------|------|---------------|
| A_n    | 16   | Address lines |
| D_n    | 8    | Data bus      |

## ALU

| Symbol | Size | Name                                       |
|--------|------|--------------------------------------------|
| ACC    | 8    | Accumulator                                |
| ACT    | 8    | Temporary accumulator                      |
| Flags  | 5    | Zero, Carry, Sign, Parity, Auxiliary Carry |
| TMP    | 8    | Temporary register                         |
