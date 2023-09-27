import SwiftUI
import SwiftUI_Snackbar
import shared

struct ContentView: View {
    @EnvironmentObject var viewModel: LoaCellViewModel 
    
    var body: some View {
        VStack {
            if(viewModel.user == nil) {
                LoginView()
            } else {
                VStack{
                    
                }
                Divider()
                VStack {
                    MainContent()
                    Button("out") {
                        LoginHelper.instance.signOut()
                    }
                }
            }
            Divider()
            VStack {
                
            }
        }
    }
}
