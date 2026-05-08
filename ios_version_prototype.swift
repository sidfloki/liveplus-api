import SwiftUI

struct Movie: Identifiable {
    let id = UUID()
    val title: String
    val category: String
    val posterName: String
}

struct iOSHomeView: View {
    let trendingMovies = [
        Movie(title: "The Batman", category: "Action", posterName: "poster1"),
        Movie(title: "Inception", category: "Fantasy", posterName: "poster2"),
        Movie(title: "Joker", category: "Drama", posterName: "poster3")
    ]
    
    var body: some View {
        NavigationView {
            ZStack {
                Color.black.edgesIgnoringSafeArea(.all)
                
                ScrollView {
                    VStack(alignment: .leading) {
                        // Header
                        HStack {
                            Text("LIVE PLUS")
                                .font(.system(size: 28, weight: .black))
                                .foregroundColor(.red)
                            Spacer()
                            Image(systemName: "magnifyingglass")
                                .foregroundColor(.white)
                            Image(systemName: "person.circle")
                                .foregroundColor(.white)
                        }
                        .padding()
                        
                        // Hero Section
                        ZStack(alignment: .bottom) {
                            Image("hero_poster")
                                .resizable()
                                .aspectRatio(contentMode: .fill)
                                .frame(height: 450)
                                .clipped()
                            
                            VStack {
                                HStack {
                                    Button(action: {}) {
                                        HStack {
                                            Image(systemName: "play.fill")
                                            Text("تشغيل")
                                        }
                                        .padding()
                                        .background(Color.white)
                                        .foregroundColor(.black)
                                        .cornerRadius(8)
                                    }
                                    
                                    Button(action: {}) {
                                        HStack {
                                            Image(systemName: "arrow.down.circle")
                                            Text("تحميل")
                                        }
                                        .padding()
                                        .background(Color.gray.opacity(0.8))
                                        .foregroundColor(.white)
                                        .cornerRadius(8)
                                    }
                                }
                            }
                            .padding(.bottom, 30)
                        }
                        
                        // Trending Section
                        Text("أفلام الأكشن")
                            .font(.headline)
                            .foregroundColor(.white)
                            .padding(.leading)
                        
                        ScrollView(.horizontal, showsIndicators: false) {
                            HStack(spacing: 15) {
                                ForEach(trendingMovies) { movie in
                                    VStack(alignment: .leading) {
                                        Image(movie.posterName)
                                            .resizable()
                                            .frame(width: 140, height: 210)
                                            .cornerRadius(10)
                                        Text(movie.title)
                                            .font(.caption)
                                            .foregroundColor(.white)
                                    }
                                }
                            }
                            .padding(.leading)
                        }
                    }
                }
            }
            .navigationBarHidden(true)
        }
    }
}

struct iOSHomeView_Previews: PreviewProvider {
    static var previews: some View {
        iOSHomeView()
    }
}
