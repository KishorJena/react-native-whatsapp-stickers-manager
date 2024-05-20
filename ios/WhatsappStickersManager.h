
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNWhatsappStickersManagerSpec.h"

@interface WhatsappStickersManager : NSObject <NativeWhatsappStickersManagerSpec>
#else
#import <React/RCTBridgeModule.h>

@interface WhatsappStickersManager : NSObject <RCTBridgeModule>
#endif

@end
