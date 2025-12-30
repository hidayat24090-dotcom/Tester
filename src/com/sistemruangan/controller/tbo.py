verbs = [
    "pinaka", "sampun", "kaucap", "ngelaksanayang", "nyarengin", "mungkah", "dados", 
    "pengalgal", "nyontoang", "nangiang", "memargi", "kauningin", "negesan", "maweweh", 
    "katambanin", "mangda", "urati", "sumeken", "pastika", "ngerauhan", "nyanggra", 
    "durung", "ngambil", "mapikayun", "nuturang", "dahat", "nyumbungan", 
    "banget", "kaaptiyang", "nyaga", "kaloktah", "kaaptiang", "ngicen", "setata", 
    "katangarin", "ngaturang", "nangian", "nincapan", "kalaksanayang", "ngemastikayang", 
    "ngaryanin", "mawesana", "tan", "surud-surud", "maosang", "keni", "makarya", "embas", 
    "majanten", "nenten", "uning", "katureksain", "wantah", "nganggen", "ngamargiang", 
    "mapangapti", "mapinunas", "ngaptiang", "nedungin", "mecikang", "tetep", "nguripan", 
    "nyihnayang", "medue", "medagang", "ngadep", "ngadepang", "meliang", "mamula", "mamaca",
    "kantun_katambanin", "melaib", "meli", "mapatung",
    "negak", "ngajeng", "luas", "manting", "nulis", "ngarit", "sirep", 
    "megending", "metanding", "memacul", "ngidih", "memace", "ngempu", 
    "melali", "megedi", "ngeling","usaha"
]

prepositions = [
    "ring", "majeng", "olih", "kantos", "sangkaning", "antuk", "sareng", "di", "ka", "jak",
    "ke", "uli"
]

det = ["punika", "puniki", "ento", "ne", "niki", "sane"]

adjectives = [
    "becik", "lantang", "satia", "negatif", "positif", 
    "kenak", "ageng", "kentel", "suci", "karesikan", "tebel", "anyar", "patut"
]

nouns = [
    "tiang", "dane", "ia", "ipun", "anak", "lanang", "kalih", "diri", "krama", "kramane", 
    "makasami", "alit-alit", "para", "yowana", "pemedek", "pembeli", "perajin", "pamikarya", 
    "rare", "angon", "memene", "adine", "ibapa", "titiang",
    "sekretaris", "daerah", "provinsi", "bali", "gubernur", "wayan", "koster", 
    "wakil", "tjokorda", "oka", "artha", "ardhana", "sukawati", "kadek", "suprapta", "meranggi", 
    "guru", "besar", "isi", "denpasar", "dosen", "bahasa", "lan", "sastra", "unud", 
    "putri", "astawa", "gede", "putra", "ariawan", "ibu", "jero", "dewa", "indra", 
    "luh", "sari", "pemerintah", "desa", "beraban", "menteri", "pariwisata",
    "pmi", "pulah", "palih", "genah", "sesuduk", "pacentokan", "layangan", "virtual", "acara", 
    "swadharma", "baga", "usaha", "bengkel", "pemilet", "utsaha", "masan", "pandemi", "pbmb", "pidarta", 
    "pasien", "covid-19", "respati", "positif", "rumah", "sakit", "kabencana", "gunung", "batur", 
    "bencana", "benjang", "pungkur", "parikrama", "konferens", "pemargi", "industri", 
    "cerpen", "palemahan", "pura", "dura", "negara", "seseleh", "budayane", "aksi", "terorisme", 
    "suksmaning", "manah", "fraksi-fraksi", "dprd", "hatinya", "pkk", "pasar", "gotong", "royong", "pangan", 
    "hari", "kerja", "angga", "karya", "kayu", "ekonomi", "parindikane", "phk", "kuliner", 
    "jeronnyane", "porsi", "abon", "pindang", "galungan", "wisatawan", "geguat", "dina", "mabasa", 
    "campuran", "bulan", "warsa", "2020", "tenaga", "penyuluh", "bendera", "merah", "putih", 
    "yayasan", "satuan", "pendidikan", "kerjasama", "sistem", "ajah-ajah", "umkm", 
    "perekonomian", "iraga", "kauratiang", "jiwa", "seni", "jukut", "jaja", "buku", "toko", "gramedia", 
    "padi", "uma", "ituni", "semengan", "umah", "kaluwihan", "inggih", "klambi", "celeng", "timpal", "timpalne",
    "nasi", "peken", "kursi", "meme", "paon", "bapa", "tegal", "bale", 
    "baju", "tukad", "surat", "pulpen", "made", "padang", "abian", "pasarean", 
    "tabia", "i", "carik", "bli", "tvne", "canang", "pekak", "carike", "pis", 
    "perpustakaane", "umahne", "art", "center", "ibi", "sanje", "sanja", "banten", "klungkung", "arya", "sekda", "bali","titi", 
]

R = {
    "VP": [
        ["V", "NP"],  # 1. V S
        ["V", "C1"],  # 2. V S O  &  3. V S Pel
        ["V", "C2"],  # 4. V S Ket
        ["V", "C3"],  # 5. V S O Ket
        ["V", "C5"],  # 6. V S O Pel 
        ["V", "C7"]   # 7. V S O Pel Ket 
    ],

    
    "C1": [["NP", "NP"], ["NP", "AP"]], 

    "C2": [["NP", "PP"]],

    "C3": [["NP", "C4"]],
    "C4": [["NP", "PP"]],

    "C5": [["NP", "C6"]],
    "C6": [["NP", "NP"], ["NP", "AP"]],

    "C7": [["NP", "C8"]],
    "C8": [["NP", "C9"]],
    "C9": [["NP", "PP"], ["AP", "PP"]],

    "NP": [
        ["N", "Det"],
        ["N", "AP"],
        ["Det", "N"],
        ["N", "N"]
    ],
    
    "AP": [["Det", "A"], ["A", "Det"]],
    
    "PP": [["P", "NP"]],
    
    "V":   [[x] for x in verbs],
    "P":   [[x] for x in prepositions],
    "Det": [[x] for x in det],
    "N":   [[x] for x in nouns],
    "A":   [[x] for x in adjectives]
}

for n_word in nouns:
    R["NP"].append([n_word])

for a_word in adjectives:
    R["AP"].append([a_word])

R["V"].append(["V", "V"])

def cykParse(w):
    n = len(w)
    print(f"\nAnalisis Kalimat ({n} kata): {w}")
    
    T = [[set([]) for j in range(n)] for i in range(n)]

    for j in range(0, n):
        current_word = w[j]
        for lhs, rules in R.items():
            for rhs in rules:
                if len(rhs) == 1 and rhs[0].lower() == current_word.lower():
                    T[j][j].add(lhs)

    for l in range(2, n + 1): 
        for i in range(0, n - l + 1): 
            j = i + l - 1 
            for k in range(i, j): 
                for lhs, rules in R.items():
                    for rhs in rules:
                        if len(rhs) == 2:
                            B = rhs[0]
                            C = rhs[1]
                            if B in T[i][k] and C in T[k + 1][j]:
                                T[i][j].add(lhs)

   
    final_cell = T[0][n-1]
    
    if "VP" in final_cell:
        print(f"[VALID] Kalimat diterima! VP ada : {final_cell}")
        return True
    else:
        print("[INVALID] Kalimat tidak sesuai dengan grammar yang ditentukan.")
        print(f"Isi sel akhir: {final_cell}")
        return False


if __name__ == "__main__":
    
    kalimat_1 = "Sampun negatif kalih diri punika".split()
    cykParse(kalimat_1)

    kalimat_2 = "Ngelaksanayang dane pacentokan baga kalih".split()
    cykParse(kalimat_2)

    kalimat_3 = "Ngadepang Luh Sari memene jukut di peken Klungkung".split()
    cykParse(kalimat_3)